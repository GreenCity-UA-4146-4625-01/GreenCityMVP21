package greencity.dto.notification.streaming;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.OffsetDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true
)
public interface StreamingNotificationDto {
    OffsetDateTime timestamp();
}
