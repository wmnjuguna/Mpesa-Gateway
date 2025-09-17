package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public record MpesaPayment(
    @JsonProperty("receiptNo")
    String receiptNo,

    @JsonProperty("tranAmount")
    double tranAmount,

    @JsonProperty("accountNo")
    String accountNo,

    @JsonProperty("tranTime")
    Date tranTime
) {}
