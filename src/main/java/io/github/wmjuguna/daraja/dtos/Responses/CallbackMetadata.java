package io.github.wmjuguna.daraja.dtos.Responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data
public class CallbackMetadata{

	@JsonProperty("Item")
	private List<ItemItem> item;
}