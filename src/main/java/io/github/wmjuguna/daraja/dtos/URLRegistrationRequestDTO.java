
package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record URLRegistrationRequestDTO(
    @JsonProperty("ConfirmationURL")
    String confirmationURL,

    @JsonProperty("ResponseType")
    String responseType,

    @JsonProperty("ShortCode")
    String shortCode,

    @JsonProperty("ValidationURL")
    String validationURL
) {}
