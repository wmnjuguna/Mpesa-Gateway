package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StkLogResponse", description = "Response payload for an STK log entry")
public record StkLogResponse(
        @JsonProperty("uuid")
        @Schema(description = "STK log UUID", example = "550e8400-e29b-41d4-a716-446655440000")
        String uuid,

        @JsonProperty("request_payload")
        @Schema(description = "Raw STK request payload", example = "{\"CheckoutRequestID\":\"ws_CO_...\"}")
        String requestPayload,

        @JsonProperty("callback_payload")
        @Schema(description = "Raw STK callback payload", example = "{\"Body\":{...}}")
        String callbackPayload,

        @JsonProperty("callback_url")
        @Schema(description = "Callback URL stored with the STK request", example = "https://example.com/callback")
        String callbackUrl
) {
}
