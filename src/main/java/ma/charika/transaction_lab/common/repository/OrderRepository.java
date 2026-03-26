package ma.charika.transaction_lab.common.repository;

import ma.charika.transaction_lab.common.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
