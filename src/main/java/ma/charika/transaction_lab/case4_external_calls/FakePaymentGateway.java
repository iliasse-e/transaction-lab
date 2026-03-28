package ma.charika.transaction_lab.case4_external_calls;

import lombok.extern.slf4j.Slf4j;
import ma.charika.transaction_lab.common.entity.Order;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FakePaymentGateway {

    public void charge(Order order) {
        log.info("Paiement simulé pour la commande {}", order.getReference());
        // On simule un paiement réussi
    }
}

