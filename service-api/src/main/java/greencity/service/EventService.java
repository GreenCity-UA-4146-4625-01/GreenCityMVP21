package greencity.service;

import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.exception.exceptions.NotFoundException;

import java.util.List;

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
     * Retrieves all events available in the system.
     *
     * @return a list of {@link EventResponseDto} objects; the list may be empty if no events exist
     */
    List<EventResponseDto> getAllEvents();

}
