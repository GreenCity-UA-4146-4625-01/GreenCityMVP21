package greencity.service;

import greencity.dto.event.EventImageDto;
import greencity.dto.event.UploadEventImageDto;
import greencity.dto.event.UploadEventImagesDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.BadRequestException;

import java.util.List;

/**
 * Provides the interface to manage {@code EventImage} entity.
 */
public interface EventImageService {

    /**
     * Uploads a single image for a specific event.
     * <p>
     * The image can optionally be marked as main, but only one main image is allowed per event.
     * The total number of images per event cannot exceed 5.
     *
     * @param dto     the {@link UploadEventImageDto} containing the image and its metadata
     * @param eventId the ID of the event to associate the image with
     * @param user    the user performing the operation
     * @return the {@link EventImageDto} representing the uploaded image
     * @throws NotFoundException   if the event with the given ID is not found
     * @throws BadRequestException if the image limit is exceeded or a main image already exists
     */
    EventImageDto uploadEventImage(UploadEventImageDto dto, Long eventId, UserVO user);

    /**
     * Uploads a list of images for a specific event.
     * <p>
     * Each image can be marked as main, but only one main image is allowed per event.
     * The total number of images per event cannot exceed 5.
     *
     * @param imagesDto the {@link UploadEventImagesDto} containing a list of image upload data
     * @param eventId   the ID of the event to associate images with
     * @param user      the user performing the operation
     * @return a list of {@link EventImageDto} representing the uploaded images
     * @throws NotFoundException   if the event with the given ID is not found
     * @throws BadRequestException if the image limit is exceeded or more than one main image is submitted
     */
    List<EventImageDto> uploadEventImages(UploadEventImagesDto imagesDto, Long eventId, UserVO user);
}