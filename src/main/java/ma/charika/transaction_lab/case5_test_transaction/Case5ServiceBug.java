package ma.charika.transaction_lab.case5_test_transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.charika.transaction_lab.common.entity.Order;
import ma.charika.transaction_lab.common.entity.OrderStatus;
import ma.charika.transaction_lab.common.entity.Payment;
import ma.charika.transaction_lab.common.entity.PaymentStatus;
import ma.charika.transaction_lab.common.repository.OrderRepository;
import ma.charika.transaction_lab.common.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Case5ServiceBug {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void processOrder() {
        Order order = Order.builder()
                .reference("CASE5-BUG-" + System.currentTimeMillis())
                .amount(400.0)
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        log.info("Commande créée (BUG)");

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        log.info("Paiement créé (BUG)");

        throw new RuntimeException("Erreur après paiement (BUG)");
    }
}
