package io.github.wmjuguna.daraja.controllers;

import io.github.wmjuguna.daraja.dtos.ClaimSTKPayment;
import io.github.wmjuguna.daraja.dtos.ResponseTemplate;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaConfirmationOrValidationResponse;
import io.github.wmjuguna.daraja.dtos.Responses.StkCallbackResponseDTO;
import io.github.wmjuguna.daraja.dtos.ValidationResponse;
import io.github.wmjuguna.daraja.entities.PaybillConfig;
import io.github.wmjuguna.daraja.services.PaybillConfigService;
import io.github.wmjuguna.daraja.services.MpesaPaymentService;
import io.github.wmjuguna.daraja.services.StkLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController @Slf4j
@RequestMapping("mobile")
public class PaymentsResource {

    private final MpesaPaymentService mpesaPaymentService;
    private final PaybillConfigService paybillConfigService;
    private final StkLogService stkLogService;

    public PaymentsResource(MpesaPaymentService mpesaPaymentService, PaybillConfigService paybillConfigService,
                            StkLogService stkLogService) {
        this.paybillConfigService = paybillConfigService;
        this.mpesaPaymentService = mpesaPaymentService;
        this.stkLogService = stkLogService;
    }

    @PostMapping("request-payment")
    public ResponseEntity<ResponseTemplate<?>> stkPushPayment(@RequestBody ClaimSTKPayment payment) {
        return ResponseEntity.ok().body(
                new ResponseTemplate<>(mpesaPaymentService.requestPayment(payment), null, null)
        );
    }

    @PostMapping("stk")
    public ResponseEntity<ResponseTemplate<?>> stkCallback(@RequestBody StkCallbackResponseDTO callback) {
        stkLogService.updateLog(callback);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("confirm/payment")
    public ResponseEntity<ResponseTemplate<?>> confirm(@RequestBody MpesaConfirmationOrValidationResponse confirmationOrValidationResponse) {
        mpesaPaymentService.recordConfirmationPayment(confirmationOrValidationResponse);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("validate/payment")
    public ResponseEntity<?> validate(@RequestBody MpesaConfirmationOrValidationResponse confirmationOrValidationResponse) {
        return ResponseEntity.ok().body(new ValidationResponse( "Accepted", "0"));
    }

    @PostMapping("configure-paybill")
    public ResponseEntity<ResponseTemplate<?>> configurePaybill(@RequestBody PaybillConfig paybillConfig) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ResponseTemplate<>(paybillConfigService.createPaybillConfiguration(paybillConfig), "Configuration created successfully", null)
                );
    }

    @GetMapping("configure-paybill")
    public ResponseEntity<ResponseTemplate<?>> allConfigurations(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaybillConfig> paybillConfigPage = paybillConfigService.getAll(pageable);
        return ResponseEntity.ok().body(
                new ResponseTemplate<>(paybillConfigPage.getContent(), "Data retrieved Successfully", null)
        );
    }

    @PutMapping("configure-paybill/{uid}")
    public ResponseEntity<ResponseTemplate<?>> updatePaybillConfiguration(@RequestBody PaybillConfig paybillConfig) {
        return ResponseEntity.status(HttpStatus.OK.value()).body(
                new ResponseTemplate<>(paybillConfigService.update(paybillConfig), "Configurations Updated Successfully", null)
        );
    }

    @GetMapping("payments/all")
    public ResponseEntity<ResponseTemplate<?>> allPayments(){
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseTemplate<>(mpesaPaymentService.allPayments(), "Payments Retrieved Successfully", null)
        );
    }
}
