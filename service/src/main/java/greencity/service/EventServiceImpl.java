package greencity.service;

import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventDateTimeDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.enums.Role;
import greencity.exception.exceptions.AccessDeniedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.EditEventRequestDtoMapper;
import greencity.repository.EventRepo;
import greencity.validator.EventDateTimeDtoValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EditEventRequestDtoMapper editEventRequestDtoMapper;
    private EventRepo eventRepository;
    private final EventDateTimeDtoValidator eventDateTimeDtoValidator;

    @Override
    @Transactional
    public EditEventRequestDto updateEventById(Long eventId, EditEventRequestDto dto, UserVO user) {

        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !event.getCreator().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are bot allowed to edit this event");
        }

        if (dto.getEventDateTimes() != null) {

            for (EventDateTimeDto dateTimeDto : dto.getEventDateTimes()) {
                eventDateTimeDtoValidator.validateAndFill(dateTimeDto);
            }
        }

        Event updatedEvent = editEventRequestDtoMapper.convert(dto);

        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setEventTypes(updatedEvent.getEventTypes());
        event.setOnlineLinks(updatedEvent.getOnlineLinks());
        event.setEventVisibility(updatedEvent.getEventVisibility());
        event.setMainImageId(updatedEvent.getMainImageId());

        event.setEventLocations(updatedEvent.getEventLocations());
        event.setEventDateTimes(updatedEvent.getEventDateTimes());
        event.setEventImages(updatedEvent.getEventImages());

        eventRepository.save(event);

        return dto;
    }
}
