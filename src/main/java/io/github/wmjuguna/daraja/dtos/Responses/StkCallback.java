package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class StkCallback{

	@JsonProperty("MerchantRequestID")
	private String merchantRequestID;

	@JsonProperty("CheckoutRequestID")
	private String checkoutRequestID;

	@JsonProperty("ResultDesc")
	private String resultDesc;

	@JsonProperty("ResultCode")
	private int resultCode;

	@JsonProperty("CallbackMetadata")
	private CallbackMetadata callbackMetadata;
}