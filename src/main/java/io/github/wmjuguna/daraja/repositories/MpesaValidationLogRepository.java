package io.github.wmjuguna.daraja.repositories;

import io.github.wmjuguna.daraja.entities.MpesaValidationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaValidationLogRepository extends JpaRepository<MpesaValidationLog, Long> {
}
