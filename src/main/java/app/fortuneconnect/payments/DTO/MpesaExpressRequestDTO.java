package app.fortuneconnect.payments.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpesaExpressRequestDTO {

	@JsonProperty("TransactionType")
	private String transactionType;

	@JsonProperty("Amount")
	private Double amount;

	@JsonProperty("CallBackURL")
	private String callBackURL;

	@JsonProperty("PhoneNumber")
	private String phoneNumber;

	@JsonProperty("PartyA")
	private String partyA;

	@JsonProperty("PartyB")
	private Integer partyB;

	@JsonProperty("AccountReference")
	private String accountReference;

	@JsonProperty("TransactionDesc")
	private String transactionDesc;

	@JsonProperty("BusinessShortCode")
	private Integer businessShortCode;

	@JsonProperty("Timestamp")
	private String timestamp;

	@JsonProperty("Password")
	@JsonFormat(pattern = "YYYYMMDDHHmmss", shape = JsonFormat.Shape.STRING)
	private String password;
}