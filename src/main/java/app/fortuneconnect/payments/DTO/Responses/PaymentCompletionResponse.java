package app.fortuneconnect.payments.DTO.Responses;

public record PaymentCompletionResponse(String transactionStatus, double amount, String paymentReference, String receiptNo){}
