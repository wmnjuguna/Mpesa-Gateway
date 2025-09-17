package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentConfirmationRequest(
	@JsonProperty("bill_reference")
	String billReference,

	@JsonProperty("receipt_no")
	String receiptNo,

	@JsonProperty("amount_received")
	double amountReceived,

	@JsonProperty("payment_method")
	String paymentMethod
) {}