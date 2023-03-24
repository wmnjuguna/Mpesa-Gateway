package app.fortuneconnect.payments.Resources;

import app.fortuneconnect.payments.DTO.ClaimSTKPayment;
import app.fortuneconnect.payments.DTO.ResponseTemplate;
import app.fortuneconnect.payments.DTO.Responses.StkCallbackResponseBody;
import app.fortuneconnect.payments.Exceptions.ExpressPaymentUnsuccessful;
import app.fortuneconnect.payments.Exceptions.ResourceNotFoundException;
import app.fortuneconnect.payments.Models.Configuration.PaybillConfig;
import app.fortuneconnect.payments.Models.Configuration.PaybillConfigService;
import app.fortuneconnect.payments.Models.MpesaPayments.MpesaPaymentService;
import app.fortuneconnect.payments.Models.StkLogs.StkLog;
import app.fortuneconnect.payments.Models.StkLogs.StkLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/v1/mpesa")
public class PaymentsResource {
    @Autowired
    private MpesaPaymentService mpesaPaymentService;

    @Autowired
    private StkLogService stkLogService;

    @Autowired
    private PaybillConfigService paybillConfigService;

    @PostMapping("request-payment")
    public ResponseEntity<ResponseTemplate> stkPushPayment(@RequestBody ClaimSTKPayment payment){
        return ResponseEntity.ok().body(
                ResponseTemplate.builder()
                        .data(mpesaPaymentService.requestPayment(payment))
                        .build()
        );
    }

    @PostMapping("stk-callback")
    public ResponseEntity<ResponseTemplate> stkCallback(@RequestBody StkCallbackResponseBody callback){
        StkLog logMono = stkLogService.retriveByMerchantId(callback.getStkCallback().getMerchantRequestID());
        logMono.setResultCode(callback.getStkCallback().getResultCode());
        if(callback.getStkCallback().getResultCode() == 0){
            logMono.setMpesaReceiptNo(callback.getStkCallback().getCallbackMetadata()
                    .getItem().stream().filter(
                            itemItem -> itemItem.getName().equalsIgnoreCase("MpesaReceiptNumber"))
                    .findFirst().get().getName());
            stkLogService.updateLog(logMono);
            return ResponseEntity.ok().body(
                    ResponseTemplate.builder().message("Payment received with receipt no").build()
        );
        } else{
            stkLogService.updateLog(logMono);
            throw new ExpressPaymentUnsuccessful("Payment Could not be completed");
        }
    }

    @PostMapping("configure-paybill")
    public ResponseEntity<ResponseTemplate> configurePaybill(@RequestBody PaybillConfig paybillConfig){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseTemplate.builder()
                                .data(paybillConfigService.createPaybillConfiguration(paybillConfig))
                                .message("Configuration created successfully")
                                .build()
                );
    }
}
