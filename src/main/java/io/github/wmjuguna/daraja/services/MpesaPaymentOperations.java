package io.github.wmjuguna.daraja.services;

import io.github.wmjuguna.daraja.dtos.ClaimSTKPayment;
import io.github.wmjuguna.daraja.dtos.Responses.MpesaConfirmationOrValidationResponse;
import io.github.wmjuguna.daraja.entities.StkLog;
import org.springframework.transaction.annotation.Transactional;

public interface MpesaPaymentOperations {
    StkLog requestPayment(ClaimSTKPayment stkPayment);

    @Transactional
    void recordConfirmationPayment(MpesaConfirmationOrValidationResponse confirmationOrValidationResponse, String rawPayload);

}
