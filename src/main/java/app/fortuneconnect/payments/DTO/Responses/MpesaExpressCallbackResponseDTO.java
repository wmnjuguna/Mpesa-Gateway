package app.fortuneconnect.payments.DTO.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MpesaExpressCallbackResponseDTO{
	@JsonProperty("Body")
	private StkCallback body;
}