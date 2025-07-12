package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.exception.exceptions.NotFoundException;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@code Event} entity.
 */
public interface EventService {

    /**
     * Creates a new event based on the provided {@link CreateEventRequestDto}.
     *
     * @param createEventRequestDto the data transfer object containing information to create the event; must not be {@code null}
     * @return the {@link EventResponseDto} representing the newly created event
     */
    EventResponseDto createEvent(CreateEventRequestDto createEventRequestDto);

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the unique identifier of the event; must not be {@code null}
     * @return the {@link EventResponseDto} representing the found event
     * @throws NotFoundException if no event with the given id exists
     */
    EventResponseDto getEventById(Long id);

    /**
     * Retrieves a paginated list of all events available in the system.
     * Supports pagination, sorting, and optional filtering if implemented.
     *
     * @param pageable the pagination and sorting information (e.g. page number, size, sort order)
     * @return a {@link PageableDto} containing a list of {@link EventResponseDto} objects;
     *         the list may be empty if no events match the criteria
     */
    PageableDto<EventResponseDto> getAllEvents(Pageable pageable);


}
