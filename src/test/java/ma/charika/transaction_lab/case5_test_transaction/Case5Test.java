package ma.charika.transaction_lab.case5_test_transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ma.charika.transaction_lab.common.repository.OrderRepository;


@SpringBootTest
@Transactional // ❌ masque le bug
class Case5Test {

    @Autowired
    private Case5ServiceBug service;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testBugMasked() {
        assertThrows(Exception.class, () -> service.processOrder());

        // ❌ Le test passe car rollback automatique
        assertEquals(0, orderRepository.count());
    }
}
