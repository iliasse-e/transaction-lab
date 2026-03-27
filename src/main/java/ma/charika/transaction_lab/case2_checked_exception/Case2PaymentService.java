package ma.charika.transaction_lab.case2_checked_exception;

import lombok.extern.slf4j.Slf4j;
import ma.charika.transaction_lab.common.entity.Order;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Case2PaymentService {

    public void processPayment(Order order) throws Exception {
        log.info("Début du paiement (CASE 2) pour la commande id={}", order.getId());

        // On simule une erreur checked
        throw new Exception("Erreur checked simulée : paiement refusé.");
    }
}
