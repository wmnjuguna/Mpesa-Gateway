package app.fortuneconnect.payments.Models.StkLogs;

import app.fortuneconnect.payments.Models.MpesaPayments.MpesaPayment;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @Getter @Setter
public class StkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sn;
    @GeneratedValue(strategy = GenerationType.UUID)
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
