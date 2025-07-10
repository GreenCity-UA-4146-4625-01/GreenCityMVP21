package greencity.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class DeleteEventResponseDto {
    @NotNull
    private boolean success;

    @NotBlank
    private String message;
}
