package io.github.wmjuguna.daraja.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mpesa_validation_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MpesaValidationLog extends BaseEntity {
    @Column(name = "validation_payload", columnDefinition = "jsonb")
    private String validationPayload;
}
