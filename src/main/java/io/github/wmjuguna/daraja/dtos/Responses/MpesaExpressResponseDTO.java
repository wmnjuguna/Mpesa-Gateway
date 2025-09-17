package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MpesaExpressResponseDTO(
	@JsonProperty("MerchantRequestID")
	String merchantRequestID,

	@JsonProperty("ResponseCode")
	Integer responseCode,

	@JsonProperty("CustomerMessage")
	String customerMessage,

	@JsonProperty("CheckoutRequestID")
	String checkoutRequestID,

	@JsonProperty("ResponseDescription")
	String responseDescription
) {}