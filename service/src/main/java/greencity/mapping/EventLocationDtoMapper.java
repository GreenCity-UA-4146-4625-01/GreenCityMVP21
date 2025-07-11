package greencity.mapping;

import greencity.dto.event.EventLocationDto;
import greencity.entity.EventLocation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EventLocationDtoMapper extends AbstractConverter<EventLocationDto, EventLocation> {
    @Override
    protected EventLocation convert(EventLocationDto dto) {
        if (dto == null) return null;

        return EventLocation.builder()
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }
}