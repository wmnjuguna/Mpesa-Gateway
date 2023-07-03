package app.fortuneconnect.payments.Models.MpesaPayments;

import app.fortuneconnect.payments.DTO.ClaimSTKPayment;
import app.fortuneconnect.payments.DTO.MpesaExpressRequestDTO;
import app.fortuneconnect.payments.DTO.Responses.MpesaConfirmationOrValidationResponse;
import app.fortuneconnect.payments.DTO.Responses.MpesaExpressResponseDTO;
import app.fortuneconnect.payments.Models.Configuration.PaybillConfig;
import app.fortuneconnect.payments.Models.Configuration.PaybillConfigService;
import app.fortuneconnect.payments.Models.StkLogs.StkLog;
import app.fortuneconnect.payments.Models.StkLogs.StkLogService;
import app.fortuneconnect.payments.Utils.Const.MpesaStaticStrings;
import app.fortuneconnect.payments.Utils.MpesaActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static app.fortuneconnect.payments.Utils.Enums.TransactionTypeEnum.CustomerPaybillOnline;

@Service @Slf4j
public class MpesaPaymentService implements MpesaPaymentOperations {

    private final MpesaPaymentRepository mpesaPaymentRepository;

    private final StkLogService stkLogService;

    private final MpesaActions actions;

    private final PaybillConfigService paybillConfigService;

    public MpesaPaymentService(MpesaPaymentRepository mpesaPaymentRepository, StkLogService stkLogService,
                               MpesaActions actions, PaybillConfigService paybillConfigService) {
        this.mpesaPaymentRepository = mpesaPaymentRepository;
        this.stkLogService = stkLogService;
        this.actions = actions;
        this.paybillConfigService = paybillConfigService;
    }

    @Transactional
    @Override
    public StkLog requestPayment(ClaimSTKPayment stkPayment){

        String timeStamp = parseDate(LocalDateTime.now());

        PaybillConfig config = this.paybillConfigService.retrievePaybillConfiguration(stkPayment.getPaybill().toString(), "no");

        String password = stkPayment.getPaybill()+new String(Base64.getDecoder().decode(config.getPassKey()))+timeStamp;
        log.info("PassKey {}", new String(Base64.getDecoder().decode(config.getPassKey())));
        log.info("TimeTamp {}", timeStamp);
        log.info("Password is {}", password);

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
        MpesaPayment payment = new MpesaPayment(null, UUID.randomUUID().toString(), null,
                confirmationOrValidationResponse.getMSISDN(), confirmationOrValidationResponse.getTransAmount(),  new Date(),
                confirmationOrValidationResponse.getBusinessShortCode(),  null, MpesaStaticStrings.MPESA_COLLECTION ,
                false, confirmationOrValidationResponse.getBillRefNumber(), MpesaStaticStrings.CREDIT,null);
        actions.callBackWithConfirmationOrFailure(confirmationOrValidationResponse.getBillRefNumber(), confirmationOrValidationResponse.getTransAmount(),
                confirmationOrValidationResponse.getTransID(),null, 0);
        mpesaPaymentRepository.save(payment);
    }

    private String parseDate(LocalDateTime date){
        ZoneId zoneId = ZoneId.of("Africa/Nairobi");
        ZonedDateTime zonedDateTime = date.atZone(zoneId);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(zonedDateTime);
    }


}
