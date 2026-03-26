package ma.charika.transaction_lab.common.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private String message;
    private T data;
}
