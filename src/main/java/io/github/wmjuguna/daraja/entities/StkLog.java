package io.github.wmjuguna.daraja.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "stk_log")
@Getter @Setter
public class StkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @UuidGenerator
    @Column(name = "uuid")
    private String stkLogUid;
    @Column(name = "merchant_request_id")
    private String merchantRequestID;
    @Column(name = "customer_message")
    private String customerMessage;
    @Column(name = "checkout_request_id")
    private String checkoutRequestID;
    @Column(name = "response_description")
    private String responseDescription;
    @Column(name = "response_code")
    private Integer responseCode;
    @Column(name = "result_code")
    private Integer resultCode;
    @Column(name = "mpesa_receipt_no")
    private String mpesaReceiptNo;
    @Column(name = "callback_url")
    private String callbackUrl;
    @Column(name = "mpesa_payment_uuid")
    private String mpesaPaymentUuid;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "mpesa_payment_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private MpesaPayment mpesaPayment;
}
