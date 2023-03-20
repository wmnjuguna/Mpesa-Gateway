package app.fortuneconnect.payments.Models.StkLogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StkLogRepository extends JpaRepository<StkLog, Long> {
    StkLog findByMerchantRequestID(String merchantrequestId);
    StkLog findByStkLogUid(String uid);
}