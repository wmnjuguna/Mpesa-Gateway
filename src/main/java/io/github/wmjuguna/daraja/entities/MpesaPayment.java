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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mpesa_payment")
@Getter
@Setter
public class MpesaPayment extends BaseEntity {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "confirmation_payload")
    private String confirmationPayload;
    @Column(name = "stk_log_uuid")
    private String stkLogUuid;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "stk_log_uuid", referencedColumnName = "uuid", insertable = false, updatable = false)
    private StkLog stkLog;
}
