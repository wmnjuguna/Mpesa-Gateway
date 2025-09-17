package io.github.wmjuguna.daraja.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApplicationResponseRecords {
    public record PaybillConfig(
        @JsonProperty("paybillNo")
        Integer paybillNo,

        @JsonProperty("paybillName")
        String paybillName
    ) {}
}
