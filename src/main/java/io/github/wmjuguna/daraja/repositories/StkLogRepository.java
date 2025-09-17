package io.github.wmjuguna.daraja.repositories;

import io.github.wmjuguna.daraja.entities.StkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkLogRepository extends JpaRepository<StkLog, Long> {
    StkLog findByMerchantRequestID(String merchantrequestId);
    StkLog findByStkLogUid(String uid);
}