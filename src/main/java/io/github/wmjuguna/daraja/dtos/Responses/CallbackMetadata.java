package io.github.wmjuguna.daraja.dtos.Responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CallbackMetadata(
	@JsonProperty("Item")
	List<ItemItem> item
) {}