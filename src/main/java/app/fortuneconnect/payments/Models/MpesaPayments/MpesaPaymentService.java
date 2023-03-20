package app.fortuneconnect.payments.Models.MpesaPayments;

import app.fortuneconnect.payments.DTO.ClaimSTKPayment;
import app.fortuneconnect.payments.DTO.MpesaExpressRequestDTO;
import app.fortuneconnect.payments.DTO.Responses.MpesaExpressResponseDTO;
import app.fortuneconnect.payments.Models.StkLogs.StkLog;
import app.fortuneconnect.payments.Models.StkLogs.StkLogService;
import app.fortuneconnect.payments.Utils.MpesaActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import static app.fortuneconnect.payments.Utils.Enums.TransactionTypeEnum.CustomerBuyGoodsOnline;

@Service @Slf4j
public class MpesaPaymentService implements MpesaPaymentOperations {

    private final MpesaPaymentRepository repository;

    private final StkLogService stkLogService;

    private final MpesaActions actions;

    @Value("${mpesa.app.consumer.passKey}")
    private String passKey;

    public MpesaPaymentService( MpesaPaymentRepository repository, StkLogService stkLogService, MpesaActions actions) {
        this.repository = repository;
        this.stkLogService = stkLogService;
        this.actions = actions;
    }

    @Override
    public StkLog requestPayment(ClaimSTKPayment stkPayment){

        String timeStamp = parseDate(new Date());

        String password = stkPayment.getPaybill()+passKey+timeStamp;

        MpesaExpressResponseDTO responseDTO = actions.lipaNaMpesaOnline(MpesaExpressRequestDTO.builder()
                .accountReference(stkPayment.getPhoneNo())
                .amount(stkPayment.getAmount())
                .businessShortCode(stkPayment.getPaybill())
                .partyA(stkPayment.getPhoneNo())
                .partyB(stkPayment.getPaybill())
                .callBackURL("https://mydomain.com/path")
                .phoneNumber(stkPayment.getPhoneNo())
                .timestamp(timeStamp)
                .transactionDesc("Payment of X")
                .transactionType(CustomerBuyGoodsOnline.getTransactioType())
                .password(
                        Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.ISO_8859_1)))
                .build());

        return stkLogService.createLog(StkLog.builder()
                .checkoutRequestID(responseDTO.getCheckoutRequestID())
                .customerMessage(responseDTO.getCustomerMessage())
                .merchantRequestID(responseDTO.getMerchantRequestID())
                .responseCode(responseDTO.getResponseCode())
                .responseDescription(responseDTO.getResponseDescription())
                .build());

//        if (responseDTO.getResponseCode() == 0) {
//            return "Request successful";
//        }
//        else {
//            return "Request unsuccessful";
//        }

    }

    private String parseDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }
}
