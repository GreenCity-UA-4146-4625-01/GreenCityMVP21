package greencity.mapping;

import greencity.dto.event.EventImageDto;
import greencity.entity.EventImage;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventImageDtoMapper extends AbstractConverter<EventImageDto, EventImage> {
    @Override
    protected EventImage convert(EventImageDto dto) {
        if (dto == null) return null;

        return EventImage.builder()
                .id(dto.getImageId())
                .isMain(Boolean.TRUE.equals(dto.getIsMain()))
                .build();
    }
}