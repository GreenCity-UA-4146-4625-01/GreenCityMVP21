package greencity.dto.event;

import jakarta.validation.constraints.FutureOrPresent;
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
    @FutureOrPresent
    private LocalDate date;

    @FutureOrPresent
    private LocalTime startTime;

    @FutureOrPresent
    private LocalTime endTime;

    @NotNull
    private boolean allDay;
}