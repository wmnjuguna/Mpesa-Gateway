package app.fortuneconnect.payments.Models.MpesaPayments;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Getter @Setter
public class MpesaPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    @GeneratedValue(strategy = GenerationType.UUID)
    private String  mpesaPaymentUid;
    @Column(length = 50, nullable = false)
    private String customerName;
    @Column(length = 15, nullable = false)
    private String phoneNumber;
    @Column(nullable = false, columnDefinition="Decimal(10,2)")
    private Double transactionAmount;
    @Column(nullable = false)
    private Date transactionTime;
    @Column(length = 6)
    private String paybillNo;
    @Column(length = 10)
    private String mpesaTransactionNo;
    @Column(length = 20)
    private String transactionType; // bulk disbursement or mpesa repayment
    private Boolean transactionStatus;
    @Column(length = 20)
    private String accountNo;
    @Column(length = 2)
    private String transactionOperation; // Whether Debit or Credit

}
