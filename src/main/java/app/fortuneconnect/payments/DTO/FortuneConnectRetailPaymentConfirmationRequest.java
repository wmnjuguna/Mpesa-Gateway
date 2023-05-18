package app.fortuneconnect.payments.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class FortuneConnectRetailPaymentConfirmationRequest{
	@JsonProperty("order_no")
	private String orderNo;

	@JsonProperty("reference")
	private String reference;

	@JsonProperty("amount_received")
	private double amountReceived;

	@JsonProperty("payment_method")
	private String paymentMethod;
}