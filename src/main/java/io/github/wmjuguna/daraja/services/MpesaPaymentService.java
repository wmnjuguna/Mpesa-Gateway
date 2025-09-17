package io.github.wmjuguna.daraja.services;

import io.github.wmjuguna.daraja.dtos.ClaimSTKPayment;
import io.github.wmjuguna.daraja.dtos.MpesaExpressRequestDTO;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaConfirmationOrValidationResponse;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaExpressResponseDTO;
import io.github.wmjuguna.daraja.dtos.Responses.PaymentCompletionResponse;
import io.github.wmjuguna.daraja.dtos.UpdatePay;
import io.github.wmjuguna.daraja.entities.MpesaPayment;
import io.github.wmjuguna.daraja.entities.PaybillConfig;
import io.github.wmjuguna.daraja.repositories.MpesaPaymentRepository;
import io.github.wmjuguna.daraja.entities.StkLog;
import io.github.wmjuguna.daraja.utils.Const.MpesaStaticStrings;
import io.github.wmjuguna.daraja.utils.MpesaActions;
import io.github.wmjuguna.daraja.config.rabbit.queues.PaymentsQueus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.wmjuguna.daraja.utils.Enums.TransactionTypeEnum.CustomerPaybillOnline;

@Service @Slf4j
@RequiredArgsConstructor
public class MpesaPaymentService implements MpesaPaymentOperations {
    private final MpesaPaymentRepository mpesaPaymentRepository;
    private final StkLogService stkLogService;
    private final MpesaActions actions;
    private final PaybillConfigService paybillConfigService;
    private final AmqpTemplate amqpTemplate;

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
        amqpTemplate.convertAndSend(PaymentsQueus.PAYMENTS_QUEUE, paymentCompletionResponse);
    }

    public List<io.github.wmjuguna.daraja.dtos.MpesaPayment> allPayments(){
        return mpesaPaymentRepository.findAll()
                .parallelStream()
                .map(payment -> new io.github.wmjuguna.daraja.dtos.MpesaPayment
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
