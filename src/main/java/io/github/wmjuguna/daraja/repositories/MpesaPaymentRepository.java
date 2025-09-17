package io.github.wmjuguna.daraja.repositories;

import io.github.wmjuguna.daraja.entities.MpesaPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaPaymentRepository extends JpaRepository<MpesaPayment, Long> {
    boolean existsByMpesaTransactionNo(String mpesaTransactionNo);
}
