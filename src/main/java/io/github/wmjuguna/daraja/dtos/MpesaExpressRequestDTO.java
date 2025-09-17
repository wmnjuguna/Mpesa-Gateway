package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MpesaExpressRequestDTO(
	@JsonProperty("TransactionType")
	String transactionType,

	@JsonProperty("Amount")
	Double amount,

	@JsonProperty("CallBackURL")
	String callBackURL,

	@JsonProperty("PhoneNumber")
	String phoneNumber,

	@JsonProperty("PartyA")
	String partyA,

	@JsonProperty("PartyB")
	Integer partyB,

	@JsonProperty("AccountReference")
	String accountReference,

	@JsonProperty("TransactionDesc")
	String transactionDesc,

	@JsonProperty("BusinessShortCode")
	Integer businessShortCode,

	@JsonProperty("Timestamp")
	String timestamp,

	@JsonProperty("Password")
	@JsonFormat(pattern = "YYYYMMDDHHmmss", shape = JsonFormat.Shape.STRING)
	String password
) {}