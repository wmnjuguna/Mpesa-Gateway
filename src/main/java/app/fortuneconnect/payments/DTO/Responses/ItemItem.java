package app.fortuneconnect.payments.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ItemItem{
	@JsonProperty("Value")
	private Object value;
	@JsonProperty("Name")
	private String name;
}