package ma.charika.transaction_lab.case1_swallowed_exception;


import ma.charika.transaction_lab.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/case1")
@RequiredArgsConstructor
public class Case1Controller {

    private final Case1ServiceBug case1ServiceBug;
    private final Case1ServiceFix case1ServiceFix;

    @GetMapping("/run-bug")
    public ApiResponse<Void> runBug() {
        case1ServiceBug.placeOrderWithSwallowedException();
        return ApiResponse.<Void>builder()
                .message("Cas 1 - BUG exécuté : la commande est créée même si le paiement échoue.")
                .build();
    }

    @GetMapping("/run-fix")
    public ApiResponse<Void> runFix() {
        try {
            case1ServiceFix.placeOrderWithProperRollback();
            return ApiResponse.<Void>builder()
                    .message("Cas 1 - FIX exécuté : rollback total si le paiement échoue.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .message("Cas 1 - FIX : exception remontée, rollback effectué.")
                    .build();
        }
    }
}
