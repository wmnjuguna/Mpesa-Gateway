package app.fortuneconnect.payments.DTO.Responses;

import java.util.Date;

public record PaymentCompletionResponse(
        Date transactionValueDate,
        double amount,
        String transactionReference,
        String accountNo,
        String payBill,
        String payer) {
}
