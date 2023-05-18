package app.fortuneconnect.payments.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class PaymentConfirmationRequest {
	@JsonProperty("bill_reference")
	private String billReference;

	@JsonProperty("receipt_no")
	private String reference;

	@JsonProperty("amount_received")
	private double amountReceived;

	@JsonProperty("payment_method")
	private String paymentMethod;
}