package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeleteEventResponseDto {
    @NotNull
    private boolean success;

    @NotBlank
    private String message;
}
