package ma.charika.transaction_lab.case4_external_calls;

import lombok.RequiredArgsConstructor;
import ma.charika.transaction_lab.common.dto.ApiResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case4")
@RequiredArgsConstructor
public class Case4Controller {

    private final Case4ServiceBug case4ServiceBug;
    private final Case4ServiceFix case4ServiceFix;

    @GetMapping("/run-bug")
    public ApiResponse<Void> runBug() {
        try {
            case4ServiceBug.placeOrder();
        } catch (Exception ignored) {}

        return ApiResponse.<Void>builder()
                .message("Cas 4 - BUG exécuté : paiement effectué mais commande rollbackée.")
                .build();
    }

    @GetMapping("/run-fix")
    public ApiResponse<Void> runFix() {
        case4ServiceFix.placeOrder();
        return ApiResponse.<Void>builder()
                .message("Cas 4 - FIX exécuté : workflow cohérent et découplé.")
                .build();
    }
}
