package ma.charika.transaction_lab.case3_self_invocation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Case3ServiceFix {

    private final Case3TransactionalWriter writer;

    public void processOrder() {
        log.info("Case 3 FIX : appel via un autre bean → proxy OK");
        writer.saveOrder(); // ✔️ transaction appliquée (méthode déportée dans une autre classe)
    }
}
