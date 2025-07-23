package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Provides the interface to manage {@code Event} entity.
 */
public interface EventService {

    /**
     * Creates a new event based on the provided {@link CreateEventRequestDto}.
     *
     * @param createEventRequestDto the data transfer object containing information to create the event; must not be {@code null}
     * @param user                  the user who is creating the event; must not be {@code null}
     * @param images                the list of image files associated with the event (optional, max 5); can be {@code null} or empty
     * @return the {@link EventResponseDto} representing the newly created event
     */
    EventResponseDto createEvent(CreateEventRequestDto createEventRequestDto, UserVO user, List<MultipartFile> images);

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
     * the list may be empty if no events match the criteria
     */
    PageableDto<EventResponseDto> getAllEvents(Pageable pageable);

    /**
     * Updates an existing event with the specified identifier using the provided data.
     *
     * @param eventId the unique identifier of the event to update; must not be {@code null}
     * @param dto     the data transfer object containing updated event information; must not be {@code null}
     * @param images  the updated list of image files (optional); can be {@code null} or empty
     * @param user    the user performing the update operation; must not be {@code null}
     * @return the {@link EventResponseDto} representing the updated event
     * @throws NotFoundException                    if the event with the specified ID does not exist
     * @throws UserHasNoPermissionToAccessException if the user is not Admin or Owner
     */
    EventResponseDto updateEventById(Long eventId, EditEventRequestDto dto, List<MultipartFile> images, UserVO user);

    /**
     * Removes a user from the list of participants of the specified event.
     *
     * @param eventId the ID of the event to attend
     * @param user    the user who wants to attend the event
     * @return the updated event details with the user's participation info
     */
    EventResponseDto assignUserToEvent(Long eventId, UserVO user);

    /**
     * Removes the given user from the participants of the specified event.
     *
     * @param eventId the ID of the event from which the user should be removed
     * @param user    the user who wants to leave the event
     * @return {@link EventResponseDto} with the updated event details
     */
    EventResponseDto unassignUserFromEvent(Long eventId, UserVO user);
}