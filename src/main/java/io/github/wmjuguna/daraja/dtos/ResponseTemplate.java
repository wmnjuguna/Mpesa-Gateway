package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "API Response Template",
        description = "Standard response wrapper for all API endpoints containing data, message, and error fields"
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseTemplate<T>(
    @JsonProperty("data")
    @Schema(
            description = "Response data payload - can be any type depending on the endpoint",
            nullable = true
    )
    T data,

    @JsonProperty("message")
    @Schema(
            description = "Success message describing the operation result",
            example = "Operation completed successfully",
            nullable = true
    )
    String message,

    @JsonProperty("error")
    @Schema(
            description = "Error message if the operation failed",
            example = "Validation failed: Invalid phone number format",
            nullable = true
    )
    String error
) {}
