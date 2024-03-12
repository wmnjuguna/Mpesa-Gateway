package app.fortuneconnect.payments.Utils.Enums;

public enum MpesaResponseType {
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String response;

    MpesaResponseType(String value) {
        this.response = value;
    }

    public String getResponse() {
        return response;
    }
}
