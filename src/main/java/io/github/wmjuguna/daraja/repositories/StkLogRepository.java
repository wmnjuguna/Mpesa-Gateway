package io.github.wmjuguna.daraja.repositories;

import io.github.wmjuguna.daraja.entities.StkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StkLogRepository extends JpaRepository<StkLog, Long> {
    @Query("""
            select log
            from StkLog log
            where function('jsonb_extract_path_text', log.callbackPayload, 'Body', 'stkCallback', 'MerchantRequestID') = :merchantRequestId
            """)
    StkLog findByMerchantRequestId(@Param("merchantRequestId") String merchantRequestId);

    @Query("""
            select log
            from StkLog log
            where function('jsonb_extract_path_text', log.callbackPayload, 'Body', 'stkCallback', 'CheckoutRequestID') = :checkoutRequestId
            """)
    StkLog findByCheckoutRequestId(@Param("checkoutRequestId") String checkoutRequestId);

    StkLog findByUuid(String uuid);
}
