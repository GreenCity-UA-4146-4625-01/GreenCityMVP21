package greencity.service;

import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventDateTimeDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.enums.Role;
import greencity.exception.exceptions.AccessDeniedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.EditEventRequestDtoMapper;
import greencity.repository.EventRepo;
import greencity.validator.EventDateTimeDtoValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing {@link Event} entities.
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final ModelMapper modelMapper;
    private final EventRepo eventRepo;
    private final FileService fileService;
    private final EditEventRequestDtoMapper editEventRequestDtoMapper;
    private EventRepo eventRepository;
    private final EventDateTimeDtoValidator eventDateTimeDtoValidator;


    /**
     * Creates a new event based on the provided {@link CreateEventRequestDto}.
     *
     * @param createEventRequestDto the DTO containing data to create the event; must not be {@code null}
     * @return the {@link EventResponseDto} representing the newly created event
     */
    @Override
    public EventResponseDto createEvent(CreateEventRequestDto createEventRequestDto) {
        Event save = modelMapper.map(createEventRequestDto, Event.class);
        eventRepo.save(save);
        return modelMapper.map(save, EventResponseDto.class);
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the unique identifier of the event; must not be {@code null}
     * @return the {@link EventResponseDto} representing the found event
     * @throws NotFoundException if no event with the given id exists
     */
    @Override
    public EventResponseDto getEventById(Long id) {
        Event event = eventRepo.findById(id).orElseThrow(
                () -> new NotFoundException("Event not found"));
        return modelMapper.map(event, EventResponseDto.class);
    }

    /**
     * Retrieves all events available in the system.
     *
     * @return a list of {@link EventResponseDto} objects; the list may be empty if no events exist
     */
    @Override
    public List<EventResponseDto> getAllEvents() {
        List<Event> events = eventRepo.findAll();
        return events.stream()
                .map(event -> modelMapper.map(event, EventResponseDto.class))
                .toList();
    }

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
