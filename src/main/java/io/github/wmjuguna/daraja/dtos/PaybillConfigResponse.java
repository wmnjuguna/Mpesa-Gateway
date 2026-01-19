package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PaybillConfigResponse", description = "Paybill configuration response")
public record PaybillConfigResponse(
        @JsonProperty("uuid")
        @Schema(description = "Paybill configuration UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        String uuid,

        @JsonProperty("paybill_no")
        @Schema(description = "Merchant paybill number", example = "174379")
        Integer paybillNo,

        @JsonProperty("organisation_name")
        @Schema(description = "Organisation name", example = "Example Ltd")
        String organisationName,

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
