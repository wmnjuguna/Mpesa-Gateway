package app.fortuneconnect.payments.Models.Configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaybillConfigRepository extends JpaRepository<PaybillConfig, Long> {
    public Optional<PaybillConfig> findByPaybillUid(String paybillUid);
    public Optional<PaybillConfig>findByPaybillNo(String paybillNo);
}
