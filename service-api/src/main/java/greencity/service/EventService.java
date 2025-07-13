package greencity.service;

import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;

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
     * @throws UserHasNoPermissionToAccessException if the user is not Admin or Owner
     */
    EventResponseDto updateEventById(Long eventId, EditEventRequestDto dto, UserVO user);

    /**
     * Uploads a single image for a specific event.
     * <p>
     * The image can optionally be marked as main, but only one main image is allowed per event.
     * The total number of images per event cannot exceed 5.
     *
     * @param dto     the {@link UploadEventImageDto} containing the image and its metadata
     * @param eventId the ID of the event to associate the image with
     * @return the {@link EventImageDto} representing the uploaded image
     * @throws NotFoundException   if the event with the given ID is not found
     * @throws BadRequestException if the image limit is exceeded or a main image already exists
     */
    EventImageDto uploadEventImage(UploadEventImageDto dto, Long eventId);

    /**
     * Uploads a list of images for a specific event.
     * <p>
     * Each image can be marked as main, but only one main image is allowed per event.
     * The total number of images per event cannot exceed 5.
     *
     * @param imagesDto the {@link UploadEventImagesDto} containing a list of image upload data
     * @param eventId   the ID of the event to associate images with
     * @return a list of {@link EventImageDto} representing the uploaded images
     * @throws NotFoundException   if the event with the given ID is not found
     * @throws BadRequestException if the image limit is exceeded or more than one main image is submitted
     */
    List<EventImageDto> uploadEventImages(UploadEventImagesDto imagesDto, Long eventId);
}
