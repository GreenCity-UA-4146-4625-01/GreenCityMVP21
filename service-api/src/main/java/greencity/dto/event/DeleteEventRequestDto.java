package greencity.dto.event;

import jakarta.validation.constraints.NotNull;

public class DeleteEventRequestDto {
    @NotNull
    private Long eventId;
}
