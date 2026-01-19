package io.github.wmjuguna.daraja.repositories;

import io.github.wmjuguna.daraja.entities.PaybillConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaybillConfigRepository extends JpaRepository<PaybillConfig, Long> {
    Optional<PaybillConfig> findByUuid(String uuid);
    Optional<PaybillConfig> findByPaybillNo(Integer paybillNo);
}
