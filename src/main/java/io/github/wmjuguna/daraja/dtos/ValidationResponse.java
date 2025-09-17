package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValidationResponse(
	@JsonProperty("ResultDesc")
	String resultDesc,

	@JsonProperty("ResultCode")
	String resultCode
) {}