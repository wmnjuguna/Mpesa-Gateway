package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data
public class ItemItem{

	@JsonProperty("Value")
	private Object value;

	@JsonProperty("Name")
	private String name;
}