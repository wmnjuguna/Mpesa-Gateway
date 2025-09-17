package io.github.wmjuguna.daraja.dtos.Responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record URLRegistrationResponseDTO(
	@JsonProperty("ConversationID")
	String conversationID,

	@JsonProperty("ResponseDescription")
	String responseDescription,

	@JsonProperty("OriginatorCoversationID")
	String originatorCoversationID
) {}