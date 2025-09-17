package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthorizationResponse(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("expires_in")
    String expiresIn
) {}
