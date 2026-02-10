package io.github.wmjuguna.daraja.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import io.github.wmjuguna.daraja.utils.MpesaResponseType;
import io.github.wmjuguna.daraja.utils.PaybillRegistrationStatus;
import io.github.wmjuguna.daraja.utils.converters.MpesaResponseTypeConverter;
import io.github.wmjuguna.daraja.utils.converters.PaybillRegistrationStatusConverter;

@Entity
@Table(name = "merchant_config")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class PaybillConfig extends BaseEntity {
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

    @Column(name = "registration_status")
    @Convert(converter = PaybillRegistrationStatusConverter.class)
    private PaybillRegistrationStatus registrationStatus;

    @Column(name = "registration_failure_reason")
    private String registrationFailureReason;
}
