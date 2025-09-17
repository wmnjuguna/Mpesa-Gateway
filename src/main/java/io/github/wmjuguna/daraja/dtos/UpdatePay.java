package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePay(
    @JsonProperty("id")
    Long id,

    @JsonProperty("customerName")
    String customerName,

    @JsonProperty("receiptNo")
    String receiptNo
) {}
