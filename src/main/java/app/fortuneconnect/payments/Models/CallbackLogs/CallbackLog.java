package app.fortuneconnect.payments.Models.CallbackLogs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity @Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class CallbackLog {
    @Id
    @GeneratedValue()
    private Long id;
    @Column(nullable = false, length = 36)
    private String logUid;
    @Column(nullable = false, unique = true, length = 50)
    private String merchantRequestID;
    @Column(nullable = false, unique = true, length = 50)
    private String checkoutRequestID;
    @Column(nullable = false)
    private String resultDesc;
    @Column(nullable = false)
    private int resultCode;
}
