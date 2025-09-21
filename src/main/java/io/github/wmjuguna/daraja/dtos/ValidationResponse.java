package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "M-Pesa Validation Response",
        description = "Response sent back to M-Pesa for payment validation requests"
)
public record ValidationResponse(
	@JsonProperty("ResultDesc")
	@Schema(
			description = "Validation result description",
			example = "Accepted",
			allowableValues = {"Accepted", "Rejected"}
	)
	String resultDesc,

	@JsonProperty("ResultCode")
	@Schema(
			description = "Validation result code - 0 for success, non-zero for failure",
			example = "0",
			allowableValues = {"0", "1"}
	)
	String resultCode
) {}