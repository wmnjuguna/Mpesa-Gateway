package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemItem(
	@JsonProperty("Value")
	Object value,

	@JsonProperty("Name")
	String name
) {}