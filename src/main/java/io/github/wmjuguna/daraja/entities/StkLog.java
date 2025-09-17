package io.github.wmjuguna.daraja.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @Getter @Setter
public class StkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    @UuidGenerator
    @Column(length = 36, unique = true, updatable = false, nullable = false)
    private String stkLogUid;
    private String merchantRequestID;
    private String customerMessage;
    private String checkoutRequestID;
    private String responseDescription;
    private Integer responseCode;
    private Integer resultCode;
    private String mpesaReceiptNo;
    private String callbackUrl;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private MpesaPayment mpesaPayment;
}
