package greencity.dto.error;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ErrorResponseDto {
    @NotNull
    private LocalDateTime timestamp;
    @Positive
    private int status;
    @NotBlank
    private String error;
    @NotBlank
    private String message;
    @NotBlank
    private String path;
}
