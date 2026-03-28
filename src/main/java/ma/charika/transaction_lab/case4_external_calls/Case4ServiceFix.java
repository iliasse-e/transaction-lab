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
public class Case4ServiceFix {

    private final OrderRepository orderRepository;
    private final FakePaymentGateway paymentGateway;
    private final FakeEmailService emailService;

    public void placeOrder() {
        // Étape 1 : créer la commande (transaction courte)
        Order order = createOrder();

        // Étape 2 : paiement (hors transaction)
        paymentGateway.charge(order);

        // Étape 3 : email (hors transaction)
        emailService.send(order);
    }

    @Transactional
    private Order createOrder() {
        Order order = Order.builder()
                .reference("CASE4-FIX-" + System.currentTimeMillis())
                .amount(300.0)
                .status(OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }
}
