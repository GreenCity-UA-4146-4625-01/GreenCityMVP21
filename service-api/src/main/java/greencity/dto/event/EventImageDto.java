package greencity.dto.event;

import jakarta.validation.constraints.NotNull;

public class EventImageDto {
    @NotNull
    private Long imageId;

    private Boolean isMain;
}
