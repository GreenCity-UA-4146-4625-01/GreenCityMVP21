package greencity.dto.event;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventDateTimeDto {
    @NotNull
    @FutureOrPresent
    private LocalDate date;

    private LocalTime startTime;
    private LocalTime endTime;

    @NotNull
    private boolean allDay;
}