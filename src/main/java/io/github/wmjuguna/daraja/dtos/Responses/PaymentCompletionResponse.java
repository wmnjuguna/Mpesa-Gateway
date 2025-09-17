package io.github.wmjuguna.daraja.dtos.Responses;

import java.util.Date;

public record PaymentCompletionResponse(
        Date transactionValueDate,
        double amount,
        String transactionReference,
        String accountNo,
        String payBill,
        String payer) {
}
