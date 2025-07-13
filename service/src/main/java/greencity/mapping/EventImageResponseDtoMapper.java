package greencity.mapping;

import greencity.dto.event.EventImageDto;
import greencity.entity.EventImage;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventImageResponseDtoMapper extends AbstractConverter<EventImage, EventImageDto> {
    @Override
    protected EventImageDto convert(EventImage eventImage) {
        if (eventImage == null) return null;

        return EventImageDto.builder()
                .imageId(eventImage.getId())
                .url(eventImage.getUrl())
                .isMain(Boolean.TRUE.equals(eventImage.getIsMain()))
                .build();
    }
}