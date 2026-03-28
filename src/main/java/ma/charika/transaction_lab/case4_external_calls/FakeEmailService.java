package ma.charika.transaction_lab.case4_external_calls;

import lombok.extern.slf4j.Slf4j;
import ma.charika.transaction_lab.common.entity.Order;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FakeEmailService {

    public void send(Order order) {
        log.info("Tentative d’envoi d’email pour {}", order.getReference());
        throw new RuntimeException("SMTP down : email non envoyé (simulation).");
    }
}
