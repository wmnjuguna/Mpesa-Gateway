package app.fortuneconnect.payments.Models.MpesaPayments;

import app.fortuneconnect.payments.DTO.ClaimSTKPayment;
import app.fortuneconnect.payments.DTO.MpesaExpressRequestDTO;
import app.fortuneconnect.payments.DTO.Responses.MpesaConfirmationOrValidationResponse;
import app.fortuneconnect.payments.DTO.Responses.MpesaExpressResponseDTO;
import app.fortuneconnect.payments.DTO.Responses.PaymentCompletionResponse;
import app.fortuneconnect.payments.DTO.UpdatePay;
import app.fortuneconnect.payments.Models.Configuration.PaybillConfig;
import app.fortuneconnect.payments.Models.Configuration.PaybillConfigService;
import app.fortuneconnect.payments.Models.StkLogs.StkLog;
import app.fortuneconnect.payments.Models.StkLogs.StkLogService;
import app.fortuneconnect.payments.Utils.Const.MpesaStaticStrings;
import app.fortuneconnect.payments.Utils.MpesaActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static app.fortuneconnect.payments.Utils.Enums.TransactionTypeEnum.CustomerPaybillOnline;

@Service @Slf4j
public class MpesaPaymentService implements MpesaPaymentOperations {

    private final MpesaPaymentRepository mpesaPaymentRepository;

    private final StkLogService stkLogService;

    private final MpesaActions actions;

    private final PaybillConfigService paybillConfigService;
    private final RestTemplate restTemplate;

    public MpesaPaymentService(MpesaPaymentRepository mpesaPaymentRepository, StkLogService stkLogService,
                               MpesaActions actions, PaybillConfigService paybillConfigService,
                               RestTemplate restTemplate) {
        this.mpesaPaymentRepository = mpesaPaymentRepository;
        this.stkLogService = stkLogService;
        this.actions = actions;
        this.paybillConfigService = paybillConfigService;
        this.restTemplate = restTemplate;
    }

    @Transactional
    @Override
    public StkLog requestPayment(ClaimSTKPayment stkPayment){

        String timeStamp = parseDate(LocalDateTime.now());

        PaybillConfig config = this.paybillConfigService.retrievePaybillConfiguration(stkPayment.getPaybill().toString(), "no");

        String password = stkPayment.getPaybill()+new String(Base64.getDecoder().decode(config.getPassKey()))+timeStamp;

        MpesaPayment payment = new MpesaPayment(null, UUID.randomUUID().toString(), null,
                stkPayment.getPhoneNo(), stkPayment.getAmount(), new Date(),
                stkPayment.getPaybill().toString(), null, MpesaStaticStrings.MPESA_STK_COLLECTION ,
                false, stkPayment.getPaymentReference(),MpesaStaticStrings.CREDIT,null);

        MpesaExpressResponseDTO responseDTO = actions.lipaNaMpesaOnline(MpesaExpressRequestDTO.builder()
                .accountReference((!Objects.isNull(stkPayment.getPaymentReference())) ? stkPayment.getPaymentReference() : stkPayment.getPhoneNo())
                .amount(stkPayment.getAmount())
                .businessShortCode(stkPayment.getPaybill())
                .partyA(stkPayment.getPhoneNo())
                .partyB(stkPayment.getPaybill())
                .callBackURL(new String(Base64.getDecoder().decode(config.getStkCallbackUrl())))
                .phoneNumber(stkPayment.getPhoneNo())
                .timestamp(timeStamp)
                .transactionDesc(stkPayment.getPaybill()+ " /REF " +stkPayment.getPhoneNo())
                .transactionType(CustomerPaybillOnline.getTransactioType())
                .password(
                        Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.ISO_8859_1)))
                .build(), new String(Base64.getDecoder().decode(config.getConsumerSecret())),
                new String(Base64.getDecoder().decode(config.getConsumerKey())));

        return stkLogService.createLog(StkLog.builder()
                .checkoutRequestID(responseDTO.getCheckoutRequestID())
                .customerMessage(responseDTO.getCustomerMessage())
                .merchantRequestID(responseDTO.getMerchantRequestID())
                .responseCode(responseDTO.getResponseCode())
                .responseDescription(responseDTO.getResponseDescription())
                .mpesaPayment(payment)
                .callbackUrl(stkPayment.getCallbackUrl())
                .build()
        );

    }

    @Override
    public void recordConfirmationPayment(MpesaConfirmationOrValidationResponse confirmationOrValidationResponse) {
        if(mpesaPaymentRepository.existsByMpesaTransactionNo(confirmationOrValidationResponse.getTransID())) return;;
        MpesaPayment payment = new MpesaPayment(null, UUID.randomUUID().toString(), confirmationOrValidationResponse.getFirstName(),
                confirmationOrValidationResponse.getMSISDN(), confirmationOrValidationResponse.getTransAmount(),  new Date(),
                confirmationOrValidationResponse.getBusinessShortCode(),  confirmationOrValidationResponse.getTransID(), MpesaStaticStrings.MPESA_COLLECTION ,
                false, confirmationOrValidationResponse.getBillRefNumber(), MpesaStaticStrings.CREDIT,null);
        actions.callBackWithConfirmationOrFailure(confirmationOrValidationResponse.getBillRefNumber(), confirmationOrValidationResponse.getTransAmount(),
                confirmationOrValidationResponse.getTransID(),null, 0);
        mpesaPaymentRepository.save(payment);
        PaymentCompletionResponse paymentCompletionResponse = new PaymentCompletionResponse(payment.getTransactionTime(),
                payment.getTransactionAmount(), payment.getMpesaTransactionNo(), payment.getAccountNo(),
                payment.getPaybillNo(), payment.getCustomerName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentCompletionResponse> request = new HttpEntity<>(paymentCompletionResponse, headers);
        try {
            String paymentEndpoint = "http://CBS/api/v1/transactions/payments?channel=MPESA";
            restTemplate.exchange(paymentEndpoint, HttpMethod.POST, request, Void.class);
        } catch (RestClientException e) {
            log.error("Error Posting Payment {}", e.getMessage());
        }
    }

    public List<app.fortuneconnect.payments.DTO.MpesaPayment> allPayments(){
        return mpesaPaymentRepository.findAll()
                .parallelStream()
                .map(payment -> new app.fortuneconnect.payments.DTO.MpesaPayment
                        (payment.getMpesaTransactionNo(), payment.getTransactionAmount(),
                                payment.getAccountNo(), payment.getTransactionTime()))
                .collect(Collectors.toList());
    }

    public void updatePay(List<UpdatePay> payList){
        List<MpesaPayment> mpesaPayments = new ArrayList<>();
        for (UpdatePay updatePay : payList) {
            MpesaPayment payment = mpesaPaymentRepository.findById(updatePay.id()).orElse(null);
            if(payment == null){
                log.info("Payment Missing with ID {}", updatePay.id());
                continue;
            }
            payment.setCustomerName(updatePay.customerName());
            payment.setMpesaTransactionNo(updatePay.receiptNo());
            mpesaPayments.add(payment);
        }
        mpesaPaymentRepository.saveAll(mpesaPayments);
    }

    private String parseDate(LocalDateTime date){
        ZoneId zoneId = ZoneId.of("Africa/Nairobi");
        ZonedDateTime zonedDateTime = date.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(zoneId);
        return zonedDateTime.format(formatter);
    }


}
