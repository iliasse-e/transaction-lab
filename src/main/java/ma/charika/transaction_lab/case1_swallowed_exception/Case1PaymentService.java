package ma.charika.transaction_lab.case1_swallowed_exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.charika.transaction_lab.common.entity.Order;
import ma.charika.transaction_lab.common.entity.Payment;
import ma.charika.transaction_lab.common.entity.PaymentStatus;
import ma.charika.transaction_lab.common.repository.PaymentRepository;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Case1PaymentService {

    private final PaymentRepository paymentRepository;

    public void processPayment(Order order) {
        log.info("Début du paiement pour la commande id={}", order.getId());

        // On simule un début de traitement (écriture en base)
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getAmount())
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        log.info("Payment PENDING enregistré pour la commande id={}", order.getId());

        // On simule une erreur métier
        throw new PaymentException("Paiement refusé par le prestataire externe (simulation).");
    }
}

