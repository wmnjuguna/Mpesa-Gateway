package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MpesaPayment(
    @JsonProperty("uuid")
    String uuid,

    @JsonProperty("receipt_no")
    String receiptNo,

    @JsonProperty("tran_amount")
    double tranAmount,

    @JsonProperty("account_no")
    String accountNo,

    @JsonProperty("tran_time")
    String tranTime
) {}
