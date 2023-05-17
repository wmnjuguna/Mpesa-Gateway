package app.fortuneconnect.payments.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ItemItem{
	@JsonProperty("Value")
	private Object value;
	@JsonProperty("Name")
	private String name;
}