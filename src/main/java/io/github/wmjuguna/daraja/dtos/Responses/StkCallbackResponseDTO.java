package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class StkCallbackResponseDTO{

	@JsonProperty("Body")
	private Body body;
}