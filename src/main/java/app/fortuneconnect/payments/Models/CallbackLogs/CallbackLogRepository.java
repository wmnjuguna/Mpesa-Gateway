package app.fortuneconnect.payments.Models.CallbackLogs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallbackLogRepository extends JpaRepository<CallbackLog, Long> {
}
