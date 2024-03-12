package app.fortuneconnect.payments.Models.MpesaPayments;

import app.fortuneconnect.payments.DTO.ClaimSTKPayment;
import app.fortuneconnect.payments.DTO.Responses.MpesaConfirmationOrValidationResponse;
import app.fortuneconnect.payments.Models.StkLogs.StkLog;
import org.springframework.transaction.annotation.Transactional;

public interface MpesaPaymentOperations {
    StkLog requestPayment(ClaimSTKPayment stkPayment);

    @Transactional
    void recordConfirmationPayment(MpesaConfirmationOrValidationResponse confirmationOrValidationResponse);

}
