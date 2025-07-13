package greencity.validator;

import greencity.dto.event.EventDateTimeDto;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class EventDateTimeDtoValidator {
    /**
     * Validates and fills default values for event date and time according to business rules:
     * - If allDay = true → set start = 00:00, end = 12:00
     * - If allDay = false:
     *     - If date is today → startTime must not be in the past
     *     - If startTime is null → set to current time + 1h
     *     - If endTime is null → set to startTime + 1h
     *     - endTime must be after startTime
     *
     * @param dto single EventDateTimeDto
     */
    public void validateAndFill(EventDateTimeDto dto) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        if (dto == null || dto.getDate() == null) {
            throw new ValidationException("Event date must not be null.");
        }

        if (dto.isAllDay()) {
            dto.setStartTime(LocalTime.MIDNIGHT);
            dto.setEndTime(LocalTime.NOON);
        } else {
            if (dto.getDate().isEqual(today)) {
                if (dto.getStartTime() != null && dto.getStartTime().isBefore(now)) {
                    throw new ValidationException("Start time cannot be in the past for today's date.");
                }
            }

            if (dto.getStartTime() == null) {
                dto.setStartTime(now.plusHours(1));
            }

            if (dto.getEndTime() == null) {
                dto.setEndTime(dto.getStartTime().plusHours(1));
            }

            if (dto.getEndTime().isBefore(dto.getStartTime())) {
                throw new ValidationException("End time must be after start time.");
            }
        }
    }
}
