package app.fortuneconnect.payments.Models.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter @Setter
public class PaybillConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 36, nullable = false, unique = true)
    private String paybillUid;
    @Column(length = 10, unique = true, nullable = false)
    private Integer paybillNo;
    @Column(length = 20, nullable = false)
    private String organisationName;
    @Column(nullable = false)
    private String consumerSecret;
    @Column(nullable = false)
    private String consumerKey;
    private String passKey;
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String confirmationUrl;
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String validationUrl;
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String stkCallbackUrl;
}
