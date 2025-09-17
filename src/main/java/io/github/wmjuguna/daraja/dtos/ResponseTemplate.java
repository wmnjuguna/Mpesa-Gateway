package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseTemplate<T>(
    @JsonProperty("data")
    T data,

    @JsonProperty("message")
    String message,

    @JsonProperty("error")
    String error
) {}
