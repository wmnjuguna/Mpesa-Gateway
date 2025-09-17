package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StkCallback(
	@JsonProperty("MerchantRequestID")
	String merchantRequestID,

	@JsonProperty("CheckoutRequestID")
	String checkoutRequestID,

	@JsonProperty("ResultDesc")
	String resultDesc,

	@JsonProperty("ResultCode")
	int resultCode,

	@JsonProperty("CallbackMetadata")
	CallbackMetadata callbackMetadata
) {}