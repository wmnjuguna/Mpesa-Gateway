package io.github.wmjuguna.daraja.utils;

public enum PaybillRegistrationStatus {
    PENDING("PENDING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String value;

    PaybillRegistrationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
