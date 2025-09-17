package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data
public class Body{

	@JsonProperty("stkCallback")
	private StkCallback stkCallback;
}