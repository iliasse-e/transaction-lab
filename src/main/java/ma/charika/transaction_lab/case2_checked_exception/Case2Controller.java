package ma.charika.transaction_lab.case2_checked_exception;

import lombok.RequiredArgsConstructor;
import ma.charika.transaction_lab.common.dto.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/case2")
@RequiredArgsConstructor
public class Case2Controller {

    private final Case2ServiceBug case2ServiceBug;
    private final Case2ServiceFix case2ServiceFix;

    @GetMapping("/run-bug")
    public ApiResponse<Void> runBug() {
        try {
            case2ServiceBug.placeOrderWithCheckedException();
        } catch (Exception e) {
            // L’exception remonte, mais Spring ne rollback pas
        }

        return ApiResponse.<Void>builder()
                .message("Cas 2 - BUG exécuté : la commande est créée malgré l’exception checked.")
                .build();
    }

    @GetMapping("/run-fix")
    public ApiResponse<Void> runFix() {
        try {
            case2ServiceFix.placeOrderWithCheckedException();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .message("Cas 2 - FIX : exception remontée, rollback effectué.")
                    .build();
        }

        return ApiResponse.<Void>builder()
                .message("Cas 2 - FIX exécuté.")
                .build();
    }
}

