package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        name = "STK Push Payment Request",
        description = "Request payload for initiating STK Push payment to customer's mobile device"
)
public record ClaimSTKPayment(
    @JsonProperty("phoneNo")
    @Schema(
            description = "Customer's mobile phone number in international format (254XXXXXXXXX)",
            example = "254712345678",
            pattern = "^254[0-9]{9}$",
            minLength = 12,
            maxLength = 12
    )
    @Min(value = 12, message = "Value does not conform to allowed length")
    @Max(value = 12, message = "Value does not conform to allowed length")
    @NotBlank(message = "Value cannot be blank")
    @NotNull(message = "Value cannot be null")
    String phoneNo,

    @JsonProperty("amount")
    @Schema(
            description = "Payment amount in KES (Kenyan Shillings)",
            example = "100.0",
            minimum = "1",
            maximum = "70000"
    )
    @NotBlank(message = "Value cannot be blank")
    @NotEmpty(message = "Value cannot be empty")
    @NotNull(message = "Value cannot be null")
    Double amount,

    @JsonProperty("paybill")
    @Schema(
            description = "Merchant paybill number (Business short code)",
            example = "174379",
            minimum = "10000",
            maximum = "999999"
    )
    @NotBlank(message = "Value cannot be blank")
    @NotEmpty(message = "Value cannot be empty")
    @NotNull(message = "Value cannot be null")
    @Min(value = 5, message = "Value does not conform to allowed length")
    @Max(value = 6, message = "Value does not conform to allowed length")
    Integer paybill,

    @JsonProperty("paymentReference")
    @Schema(
            description = "Unique payment reference/account number for this transaction",
            example = "ORDER123",
            minLength = 1,
            maxLength = 20
    )
    @NotBlank(message = "Value cannot be blank")
    @NotEmpty(message = "Value cannot be empty")
    @NotNull(message = "Value cannot be null")
    @Min(value = 5, message = "Value does not conform to allowed length")
    @Max(value = 6, message = "Value does not conform to allowed length")
    String paymentReference,

    @JsonProperty("callbackUrl")
    @Schema(
            description = "Optional callback URL where payment status will be sent after completion",
            example = "https://yourdomain.com/callback",
            format = "uri"
    )
    String callbackUrl
) {}
