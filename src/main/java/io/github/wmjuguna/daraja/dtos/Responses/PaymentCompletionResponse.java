package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public record PaymentCompletionResponse(
        @JsonProperty("transactionValueDate")
        Date transactionValueDate,

        @JsonProperty("amount")
        double amount,

        @JsonProperty("transactionReference")
        String transactionReference,

        @JsonProperty("accountNo")
        String accountNo,

        @JsonProperty("payBill")
        String payBill,

        @JsonProperty("payer")
        String payer
) {}
