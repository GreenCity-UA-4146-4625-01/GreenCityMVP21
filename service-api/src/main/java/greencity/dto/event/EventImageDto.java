package greencity.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public class EventImageDto {
    @NotNull
    private Long imageId;

    @Max(1)
    private Boolean isMain;
}
