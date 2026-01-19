package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MpesaPayment(
    @JsonProperty("uuid")
    String uuid,

    @JsonProperty("receiptNo")
    String receiptNo,

    @JsonProperty("tranAmount")
    double tranAmount,

    @JsonProperty("accountNo")
    String accountNo,

    @JsonProperty("tranTime")
    String tranTime
) {}
