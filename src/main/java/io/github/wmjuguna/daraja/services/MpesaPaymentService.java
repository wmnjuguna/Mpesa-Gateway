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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    @Override
    public StkLog requestPayment(ClaimSTKPayment stkPayment){

        String timeStamp = parseDate(LocalDateTime.now());

        PaybillConfig config = this.paybillConfigService.retrievePaybillConfiguration(stkPayment.paybill().toString(), "no");

        String password = stkPayment.paybill()+new String(Base64.getDecoder().decode(config.getPassKey()))+timeStamp;

        MpesaPayment payment = new MpesaPayment(null, UUID.randomUUID().toString(), null,
                stkPayment.phoneNo(), stkPayment.amount(), new Date(),
                stkPayment.paybill().toString(), null, MpesaStaticStrings.MPESA_STK_COLLECTION ,
                false, stkPayment.paymentReference(),MpesaStaticStrings.CREDIT,null,null);

        MpesaExpressResponseDTO responseDTO = actions.lipaNaMpesaOnline(new MpesaExpressRequestDTO(
                CustomerPaybillOnline.getTransactioType(),
                stkPayment.amount(),
                new String(Base64.getDecoder().decode(config.getStkCallbackUrl())),
                stkPayment.phoneNo(),
                stkPayment.phoneNo(),
                stkPayment.paybill(),
                (!Objects.isNull(stkPayment.paymentReference())) ? stkPayment.paymentReference() : stkPayment.phoneNo(),
                stkPayment.paybill()+ " /REF " +stkPayment.phoneNo(),
                stkPayment.paybill(),
                timeStamp,
                Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.ISO_8859_1))
        ), new String(Base64.getDecoder().decode(config.getConsumerSecret())),
                new String(Base64.getDecoder().decode(config.getConsumerKey())));

        return stkLogService.createLog(StkLog.builder()
                .checkoutRequestID(responseDTO.checkoutRequestID())
                .customerMessage(responseDTO.customerMessage())
                .merchantRequestID(responseDTO.merchantRequestID())
                .responseCode(responseDTO.responseCode())
                .responseDescription(responseDTO.responseDescription())
                .mpesaPayment(payment)
                .callbackUrl(stkPayment.callbackUrl())
                .build()
        );

    }

    @Override
    public void recordConfirmationPayment(MpesaConfirmationOrValidationResponse confirmationOrValidationResponse) {
        if(mpesaPaymentRepository.existsByMpesaTransactionNo(confirmationOrValidationResponse.transID())) return;;
        MpesaPayment payment = new MpesaPayment(null, UUID.randomUUID().toString(), confirmationOrValidationResponse.firstName(),
                confirmationOrValidationResponse.mSISDN(), confirmationOrValidationResponse.transAmount(),  new Date(),
                confirmationOrValidationResponse.businessShortCode(),  confirmationOrValidationResponse.transID(), MpesaStaticStrings.MPESA_COLLECTION ,
                false, confirmationOrValidationResponse.billRefNumber(), MpesaStaticStrings.CREDIT,null,null);
        actions.callBackWithConfirmationOrFailure(confirmationOrValidationResponse.billRefNumber(), confirmationOrValidationResponse.transAmount(),
                confirmationOrValidationResponse.transID(),null, 0);
        mpesaPaymentRepository.save(payment);
        log.info("Payment confirmed: TransactionID={}, Amount={}, Reference={}",
                payment.getMpesaTransactionNo(), payment.getTransactionAmount(), payment.getAccountNo());
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
