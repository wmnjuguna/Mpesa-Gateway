package io.github.wmjuguna.daraja.repositories;

import io.github.wmjuguna.daraja.entities.MpesaPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaPaymentRepository extends JpaRepository<MpesaPayment, Long> {
    @Query("""
            select (count(payment) > 0)
            from MpesaPayment payment
            where function('jsonb_extract_path_text', payment.confirmationPayload, 'TransID') = :transId
            """)
    boolean existsByTransId(@Param("transId") String transId);
}
