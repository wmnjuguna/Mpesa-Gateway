package app.fortuneconnect.payments.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ClaimSTKPayment {
    @JsonProperty("phoneNo")
    @Min(value = 12, message = "Value does not conform to allowed length")
    @Max(value = 12, message = "Value does not conform to allowed length")
    @NotBlank(message = "Value cannot be blank")
    @NotNull(message =  "Value cannot be null")
    private String phoneNo;

    @JsonProperty("amount")
    @NotBlank(message =  "Value cannot be blank")
    @NotEmpty(message =  "Value cannot be emoty")
    @NotNull(message =  "Value cannot be null")
    private Double amount;

    @JsonProperty(value = "paybill", required = true)
    @NotBlank(message =  "Value cannot be blank")
    @NotEmpty(message =  "Value cannot be emoty")
    @NotNull(message =  "Value cannot be null")
    @Min(value = 5, message = "Value does not conform to allowed length")
    @Max(value = 6, message = "Value does not conform to allowed length")
    private Integer paybill;
}
