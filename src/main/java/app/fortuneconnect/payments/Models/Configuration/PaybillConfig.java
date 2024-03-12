package app.fortuneconnect.payments.Models.Configuration;

import app.fortuneconnect.payments.Utils.Enums.MpesaResponseType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter @Setter
public class PaybillConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access=JsonProperty.Access.READ_ONLY)
    private Long id;
    @Column(length = 36, nullable = false, unique = true, updatable = false)
    @UuidGenerator
    private String paybillUid;
    @Column(length = 10, unique = true, nullable = false)
    private Integer paybillNo;
    @Column(length = 100, nullable = false)
    private String organisationName;
    @Column(nullable = false)
    private String consumerSecret;
    @Column(nullable = false)
    private String consumerKey;
    private String passKey;
    private String confirmationUrl;
    private String validationUrl;
    private String stkCallbackUrl;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MpesaResponseType responseType;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdAt;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column( nullable = false)
    @JsonIgnore
    private Date updatedAt;
}
