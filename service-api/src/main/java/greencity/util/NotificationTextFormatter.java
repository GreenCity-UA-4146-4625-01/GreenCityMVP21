package greencity.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationTextFormatter {
    public static String formatNewReplyText(String replierName, String objectType, String objectTitle, LocalDateTime repliedAt) {
        String timePart = formatDateTime(repliedAt);
        return String.format("%s replied to your comment on the %s %s. %s",
                replierName, objectType, objectTitle, timePart);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        if (dateTime.toLocalDate().equals(now.toLocalDate())) {
            return "Today " + dateTime.format(timeFormatter);
        } else if (dateTime.toLocalDate().equals(now.toLocalDate().minusDays(1))) {
            return "Yesterday " + dateTime.format(timeFormatter);
        } else {
            DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm a");
            return dateTime.format(fullFormatter);
        }
    }
}
