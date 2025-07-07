package greencity.dto.error;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
public class ErrorResponseDto {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
