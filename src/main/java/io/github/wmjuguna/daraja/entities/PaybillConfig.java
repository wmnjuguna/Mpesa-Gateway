package io.github.wmjuguna.daraja.entities;

import io.github.wmjuguna.daraja.utils.Enums.MpesaResponseType;
import io.github.wmjuguna.daraja.utils.converters.MpesaResponseTypeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Entity
@Table(name = "merchant_config")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter @Setter
public class PaybillConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access=JsonProperty.Access.READ_ONLY)
    private Long id;
    @Column(name = "uuid")
    @UuidGenerator
    private String paybillUid;
    @Column(name = "merchant_number")
    private Integer paybillNo;
    @Column(name = "organisation_name")
    private String organisationName;
    @Column(name = "consumer_secret")
    private String consumerSecret;
    @Column(name = "consumer_key")
    private String consumerKey;
    @Column(name = "pass_key")
    private String passKey;
    @Column(name = "confirmation_url")
    private String confirmationUrl;
    @Column(name = "validation_url")
    private String validationUrl;
    @Column(name = "stk_callback_url")
    private String stkCallbackUrl;
    @Column(name = "response_type")
    @Convert(converter = MpesaResponseTypeConverter.class)
    private MpesaResponseType responseType;
    @CreationTimestamp
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdAt;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @JsonIgnore
    private Date updatedAt;
}
