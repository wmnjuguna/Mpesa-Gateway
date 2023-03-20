package app.fortuneconnect.payments.Models.MpesaPayments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpesaPaymentRepository extends JpaRepository<MpesaPayment, Long> {
}
