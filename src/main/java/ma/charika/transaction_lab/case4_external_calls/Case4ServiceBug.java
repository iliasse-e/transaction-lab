package ma.charika.transaction_lab.case4_external_calls;

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
public class Case4ServiceBug {

    private final OrderRepository orderRepository;
    private final FakePaymentGateway paymentGateway;
    private final FakeEmailService emailService;

    @Transactional
    public void placeOrder() {
        Order order = Order.builder()
                .reference("CASE4-BUG-" + System.currentTimeMillis())
                .amount(300.0)
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        log.info("Commande créée (BUG)");

        paymentGateway.charge(order); // non rollbackable
        log.info("Paiement effectué (BUG)");

        emailService.send(order); // échoue → rollback DB
        log.info("Email envoyé (BUG)");
    }
}
