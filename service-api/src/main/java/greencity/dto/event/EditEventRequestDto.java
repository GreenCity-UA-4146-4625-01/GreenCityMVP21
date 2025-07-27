package greencity.dto.event;

import greencity.enums.EventType;
import greencity.enums.EventVisibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EditEventRequestDto {

    private Long eventId;

    @Size(max = 70)
    private String title;

    @Size(min = 20, max = 63206)
    private String description;

    private EventVisibility visibility;

    private Set<EventType> eventTypes;

    private List<EventLocationDto> locations;

    private List<@URL String> onlineLinks;

    @Size(min = 1, max = 7)
    @Valid
    private List<EventDateTimeDto> eventDateTimes;

}
