package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StkCallbackResponseBody(
	@JsonProperty("stkCallback")
	StkCallback stkCallback
) {}