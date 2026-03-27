package ma.charika.transaction_lab.case3_self_invocation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.charika.transaction_lab.common.entity.Order;
import ma.charika.transaction_lab.common.entity.OrderStatus;
import ma.charika.transaction_lab.common.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Case3ServiceBug {

    private final OrderRepository orderRepository;

    public void processOrder() {
        log.info("Case 3 BUG : appel interne → @Transactional ignoré");
        saveOrder(); // ❌ self-invocation
    }

    @Transactional
    public void saveOrder() {
        Order order = Order.builder()
                .reference("CASE3-BUG-" + System.currentTimeMillis())
                .amount(200.0)
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        log.info("Commande enregistrée (BUG)");

        throw new RuntimeException("Erreur simulée dans saveOrder()");
    }
}
