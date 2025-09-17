package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StkCallbackResponseDTO(
	@JsonProperty("Body")
	Body body
) {}