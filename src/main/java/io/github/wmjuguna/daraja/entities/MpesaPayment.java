package io.github.wmjuguna.daraja.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Entity @Getter @Setter
public class MpesaPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    @Column(nullable = false, length = 36, unique = true, updatable = false)
    @UuidGenerator
    private String  mpesaPaymentUid;
    @Column(length = 50)
    private String customerName;
    @Column(length = 15, nullable = false)
    private String phoneNumber;
    @Column(nullable = false, columnDefinition="Decimal(10,2)")
    private Double transactionAmount;
    @Column(nullable = false)
    private Date transactionTime;
    @Column(length = 10)
    private String paybillNo;
    @Column(length = 10)
    private String mpesaTransactionNo;
    @Column(length = 20)
    private String transactionType;
    private Boolean transactionStatus;
    @Column(length = 20)
    private String accountNo;
    @Column(length = 2)
    private String transactionOperation;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private StkLog stkLog;
}
