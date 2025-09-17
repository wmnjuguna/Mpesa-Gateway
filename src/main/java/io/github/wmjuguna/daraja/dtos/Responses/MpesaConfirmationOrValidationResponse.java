package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data @AllArgsConstructor @NoArgsConstructor @ToString
public class MpesaConfirmationOrValidationResponse{

	@JsonProperty("TransactionType")
	private String transactionType;

	@JsonProperty("BillRefNumber")
	private String billRefNumber;

	@JsonProperty("MSISDN")
	private String mSISDN;

	@JsonProperty("FirstName")
	private String firstName;

	@JsonProperty("MiddleName")
	private String middleName;

	@JsonProperty("BusinessShortCode")
	private String businessShortCode;

	@JsonProperty("OrgAccountBalance")
	private String orgAccountBalance;

	@JsonProperty("TransAmount")
	private double transAmount;

	@JsonProperty("ThirdPartyTransID")
	private String thirdPartyTransID;

	@JsonProperty("InvoiceNumber")
	private String invoiceNumber;

	@JsonProperty("LastName")
	private String lastName;

	@JsonProperty("TransID")
	private String transID;

	@JsonProperty("TransTime")
	private String transTime;
}