package app.fortuneconnect.payments.DTO;

public record MpesaPayment(String receiptNo, double tranAmount, String accountNo, java.util.Date tranTime) {
}
