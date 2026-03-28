package ma.charika.transaction_lab.case5_test_transaction;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ma.charika.transaction_lab.common.repository.OrderRepository;

@SpringBootTest
class Case5RealisticTest {

    @Autowired
    private Case5ServiceBug service;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testRealBehavior() {
        try {
            service.processOrder();
        } catch (Exception ignored) {}

        // ✔️ En prod, la commande existe si exception checked
        assertTrue(orderRepository.count() >= 1);
    }
}
