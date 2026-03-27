package ma.charika.transaction_lab.case2_checked_exception;

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
public class Case2ServiceFix {

    private final OrderRepository orderRepository;
    private final Case2PaymentService paymentService;

    @Transactional(rollbackFor = Exception.class)
    public void placeOrderWithCheckedException() throws Exception {
        Order order = Order.builder()
                .reference("CASE2-FIX-" + System.currentTimeMillis())
                .amount(150.0)
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        log.info("Commande créée (FIX) : id={}, ref={}", order.getId(), order.getReference());

        // ✔️ Exception checked → rollback grâce à rollbackFor
        paymentService.processPayment(order);

        log.info("Fin de placeOrderWithCheckedException (FIX) : COMMIT uniquement si tout réussit.");
    }
}
