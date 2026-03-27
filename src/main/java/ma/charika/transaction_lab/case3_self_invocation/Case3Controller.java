package ma.charika.transaction_lab.case3_self_invocation;

import ma.charika.transaction_lab.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case3")
@RequiredArgsConstructor
public class Case3Controller {

    private final Case3ServiceBug case3ServiceBug;
    private final Case3ServiceFix case3ServiceFix;

    @GetMapping("/run-bug")
    public ApiResponse<Void> runBug() {
        try {
            case3ServiceBug.processOrder();
        } catch (Exception ignored) {}

        return ApiResponse.<Void>builder()
                .message("Cas 3 - BUG exécuté : la commande est créée malgré l’erreur.")
                .build();
    }

    @GetMapping("/run-fix")
    public ApiResponse<Void> runFix() {
        try {
            case3ServiceFix.processOrder();
        } catch (Exception ignored) {}

        return ApiResponse.<Void>builder()
                .message("Cas 3 - FIX exécuté : rollback total.")
                .build();
    }
}
