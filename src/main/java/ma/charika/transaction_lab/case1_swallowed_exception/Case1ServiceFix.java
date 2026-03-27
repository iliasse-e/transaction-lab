package ma.charika.transaction_lab.case1_swallowed_exception;

import ma.charika.transaction_lab.common.entity.Order;
import ma.charika.transaction_lab.common.entity.OrderStatus;
import ma.charika.transaction_lab.common.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Case1ServiceFix {

    private final OrderRepository orderRepository;
    private final Case1PaymentService paymentService;

    @Transactional
    public void placeOrderWithProperRollback() {
        Order order = Order.builder()
                .reference("CASE1-FIX-" + System.currentTimeMillis())
                .amount(100.0)
                .status(OrderStatus.PENDING)
                .build();

        order = orderRepository.save(order);
        log.info("Commande créée (FIX) : id={}, ref={}", order.getId(), order.getReference());

        try {
            paymentService.processPayment(order);
        } catch (PaymentException e) {
            log.error("Erreur lors du paiement (FIX) : {}", e.getMessage());
            // ✔️ On relance l’exception pour que Spring rollback
            throw e;
        }

        log.info("Fin de placeOrderWithProperRollback (FIX) : Spring va COMMIT uniquement si tout a réussi.");
    }
}
