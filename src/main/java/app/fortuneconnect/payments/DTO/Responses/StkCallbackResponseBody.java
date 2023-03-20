package app.fortuneconnect.payments.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StkCallbackResponseBody{
	@JsonProperty("stkCallback")
	private StkCallback stkCallback;
}