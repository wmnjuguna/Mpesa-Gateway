package app.fortuneconnect.payments.Models.MpesaPayments;

import app.fortuneconnect.payments.DTO.ClaimSTKPayment;
import app.fortuneconnect.payments.Models.StkLogs.StkLog;

public interface MpesaPaymentOperations {
    StkLog requestPayment(ClaimSTKPayment stkPayment);

}
