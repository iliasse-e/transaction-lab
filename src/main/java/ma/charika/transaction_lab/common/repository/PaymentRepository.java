package ma.charika.transaction_lab.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ma.charika.transaction_lab.common.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
