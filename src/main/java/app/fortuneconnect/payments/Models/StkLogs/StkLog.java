package app.fortuneconnect.payments.Models.StkLogs;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
