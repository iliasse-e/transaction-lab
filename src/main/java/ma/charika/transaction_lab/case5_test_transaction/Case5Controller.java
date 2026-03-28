package ma.charika.transaction_lab.case5_test_transaction;

import lombok.RequiredArgsConstructor;
import ma.charika.transaction_lab.common.dto.ApiResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case5")
@RequiredArgsConstructor
public class Case5Controller {

    private final Case5ServiceBug case5ServiceBug;
    private final Case5ServiceFix case5ServiceFix;

    @GetMapping("/run-bug")
    public ApiResponse<Void> runBug() {
        try {
            case5ServiceBug.processOrder();
        } catch (Exception ignored) {}

        return ApiResponse.<Void>builder()
                .message("Cas 5 - BUG exécuté : en test tout passe, en prod incohérence possible.")
                .build();
    }

    @GetMapping("/run-fix")
    public ApiResponse<Void> runFix() {
        try {
            case5ServiceFix.processOrder();
        } catch (Exception ignored) {}

        return ApiResponse.<Void>builder()
                .message("Cas 5 - FIX exécuté : comportement réaliste testé correctement.")
                .build();
    }
}
