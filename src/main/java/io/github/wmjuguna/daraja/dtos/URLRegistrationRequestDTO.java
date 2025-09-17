
package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class URLRegistrationRequestDTO {
    @JsonProperty("ConfirmationURL")
    private String confirmationURL;
    @JsonProperty("ResponseType")
    private String responseType;
    @JsonProperty("ShortCode")
    private String shortCode;
    @JsonProperty("ValidationURL")
    private String validationURL;
}
