package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PaybillConfigRequest", description = "Paybill configuration request payload")
public record PaybillConfigRequest(
        @JsonProperty("paybill_no")
        @Schema(description = "Merchant paybill number", example = "174379")
        Integer paybillNo,

        @JsonProperty("organisation_name")
        @Schema(description = "Organisation name", example = "Example Ltd")
        String organisationName,

        @JsonProperty("consumer_secret")
        @Schema(description = "Consumer secret", example = "your-consumer-secret")
        String consumerSecret,

        @JsonProperty("consumer_key")
        @Schema(description = "Consumer key", example = "your-consumer-key")
        String consumerKey,

        @JsonProperty("pass_key")
        @Schema(description = "Pass key", example = "your-passkey-here")
        String passKey,

        @JsonProperty("confirmation_url")
        @Schema(description = "Confirmation URL", example = "https://example.com/mobile/confirm/payment")
        String confirmationUrl,

        @JsonProperty("validation_url")
        @Schema(description = "Validation URL", example = "https://example.com/mobile/validate/payment")
        String validationUrl,

        @JsonProperty("stk_callback_url")
        @Schema(description = "STK callback URL", example = "https://example.com/mobile/stk")
        String stkCallbackUrl,

        @JsonProperty("response_type")
        @Schema(description = "Response type", example = "Completed")
        String responseType
) {
}
