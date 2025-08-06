package greencity.formatter;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class NotificationFormatter {
    public String formatDateAndTime(ZonedDateTime dateTime) {
        LocalDate today = LocalDate.now();
        LocalDate notificationDate = dateTime.toLocalDate();

        if (notificationDate.isEqual(today)) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            return "Today " + dateTime.format(timeFormatter);
        }

        if (notificationDate.isEqual(today.minusDays(1))) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            return "Yesterday " + dateTime.format(timeFormatter);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy h:mm a");
        return dateTime.format(dateFormatter);
    }
}