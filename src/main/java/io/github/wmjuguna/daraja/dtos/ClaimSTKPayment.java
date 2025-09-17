package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public record ClaimSTKPayment(
    @JsonProperty("phoneNo")
    @Min(value = 12, message = "Value does not conform to allowed length")
    @Max(value = 12, message = "Value does not conform to allowed length")
    @NotBlank(message = "Value cannot be blank")
    @NotNull(message = "Value cannot be null")
    String phoneNo,

    @JsonProperty("amount")
    @NotBlank(message = "Value cannot be blank")
    @NotEmpty(message = "Value cannot be empty")
    @NotNull(message = "Value cannot be null")
    Double amount,

    @JsonProperty("paybill")
    @NotBlank(message = "Value cannot be blank")
    @NotEmpty(message = "Value cannot be empty")
    @NotNull(message = "Value cannot be null")
    @Min(value = 5, message = "Value does not conform to allowed length")
    @Max(value = 6, message = "Value does not conform to allowed length")
    Integer paybill,

    @JsonProperty("paymentReference")
    @NotBlank(message = "Value cannot be blank")
    @NotEmpty(message = "Value cannot be empty")
    @NotNull(message = "Value cannot be null")
    @Min(value = 5, message = "Value does not conform to allowed length")
    @Max(value = 6, message = "Value does not conform to allowed length")
    String paymentReference,

    @JsonProperty("callbackUrl")
    String callbackUrl
) {}
