package greencity.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EventDateTimeDto {
    @NotNull
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotNull
    private boolean allDay;
}