package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MpesaExpressCallbackResponseDTO(
	@JsonProperty("Body")
	StkCallback body
) {}