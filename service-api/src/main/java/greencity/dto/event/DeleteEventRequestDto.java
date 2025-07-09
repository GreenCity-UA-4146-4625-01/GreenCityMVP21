package greencity.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class DeleteEventRequestDto {
    @NotNull
    private Long eventId;
}
