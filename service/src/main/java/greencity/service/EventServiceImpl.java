package greencity.service;

import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.entity.Event;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
}
