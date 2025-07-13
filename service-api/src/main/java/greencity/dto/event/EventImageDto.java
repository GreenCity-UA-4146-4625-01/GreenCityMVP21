package greencity.dto.event;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventImageDto {
    @NotNull
    private Long imageId;

    private String url;

    @Max(1)
    private Boolean isMain;
}
