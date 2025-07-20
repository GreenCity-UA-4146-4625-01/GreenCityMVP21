package greencity.dto.event;

import greencity.enums.EventType;
import greencity.enums.EventVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventResponseDto {

    @NotNull
    private Long eventId;

    @NotBlank
    @Size(max = 70)
    private String title;

    @NotBlank
    @Size(min = 20, max = 63206)
    private String description;

    @NotNull
    private EventVisibility visibility;

    @NotEmpty
    private Set<EventType> eventTypes;

    @Size(min = 1, max = 7)
    private List<@Valid EventDateTimeDto> eventDateTimes;

    private List<@Valid EventLocationDto> locations;

    private List<@URL String> onlineLinks;

    private Long mainImageId;

    @NotNull
    private LocalDateTime createdAt;
}
