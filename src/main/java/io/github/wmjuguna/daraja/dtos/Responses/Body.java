package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Body(
	@JsonProperty("stkCallback")
	StkCallback stkCallback
) {}