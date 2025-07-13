package greencity.service;

import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.AccessDeniedException;
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


    /**
     * Updates an existing event with the specified identifier using the provided data.
     *
     * @param eventId the unique identifier of the event to update; must not be {@code null}
     * @param dto     the data transfer object containing updated event information; must not be {@code null}
     * @param user    the user performing the update operation; must not be {@code null}
     * @return the {@link EditEventRequestDto} representing the updated event
     * @throws NotFoundException if the event with the specified ID does not exist
     * @throws AccessDeniedException if the user is not Admin or Owner
     */
    EditEventRequestDto updateEventById(Long eventId, EditEventRequestDto dto, UserVO user);
}
