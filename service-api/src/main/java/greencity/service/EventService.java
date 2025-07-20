package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     *         the list may be empty if no events match the criteria
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
     * Deletes an event with the specified identifier by a given user.
     *
     * @param id   the unique identifier of the event to delete; must not be {@code null}
     * @param user the user performing the delete operation; must not be {@code null}
     * @throws NotFoundException                    if the event with the specified ID does not exist
     * @throws UserHasNoPermissionToAccessException if the user is not Admin or Owner
     */
    void deleteEventById(Long id, UserVO user);

}