package io.github.wmjuguna.daraja.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stk_log")
@Getter
@Setter
public class StkLog extends BaseEntity {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "callback_payload")
    private String callbackPayload;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload")
    private String requestPayload;
    @Column(name = "callback_url")
    private String callbackUrl;
    @Column(name = "mpesa_payment_uuid")
    private String mpesaPaymentUuid;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "mpesa_payment_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private MpesaPayment mpesaPayment;
}
