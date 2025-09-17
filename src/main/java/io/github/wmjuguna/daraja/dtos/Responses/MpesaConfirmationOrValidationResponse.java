package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MpesaConfirmationOrValidationResponse(
	@JsonProperty("TransactionType")
	String transactionType,

	@JsonProperty("BillRefNumber")
	String billRefNumber,

	@JsonProperty("MSISDN")
	String mSISDN,

	@JsonProperty("FirstName")
	String firstName,

	@JsonProperty("MiddleName")
	String middleName,

	@JsonProperty("BusinessShortCode")
	String businessShortCode,

	@JsonProperty("OrgAccountBalance")
	String orgAccountBalance,

	@JsonProperty("TransAmount")
	double transAmount,

	@JsonProperty("ThirdPartyTransID")
	String thirdPartyTransID,

	@JsonProperty("InvoiceNumber")
	String invoiceNumber,

	@JsonProperty("LastName")
	String lastName,

	@JsonProperty("TransID")
	String transID,

	@JsonProperty("TransTime")
	String transTime
) {}