package io.github.wmjuguna.daraja.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Builder
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "mpesa_payment")
@Getter @Setter
public class MpesaPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uuid")
    @UuidGenerator
    private String  mpesaPaymentUid;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "transaction_amount")
    private Double transactionAmount;
    @Column(name = "transaction_time")
    private Date transactionTime;
    @Column(name = "paybill_no")
    private String paybillNo;
    @Column(name = "mpesa_transaction_no")
    private String mpesaTransactionNo;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "transaction_status")
    private Boolean transactionStatus;
    @Column(name = "account_no")
    private String accountNo;
    @Column(name = "transaction_operation")
    private String transactionOperation;
    @Column(name = "stk_log_uuid")
    private String stkLogUuid;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "stk_log_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private StkLog stkLog;
}
