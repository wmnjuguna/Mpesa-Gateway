package io.github.wmjuguna.daraja.utils.Enums;

public enum DebitCreditEnum {
    CREDIT("CR"),
    DEBIT("DR");

    private final String operation;

    DebitCreditEnum(String operation){
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
