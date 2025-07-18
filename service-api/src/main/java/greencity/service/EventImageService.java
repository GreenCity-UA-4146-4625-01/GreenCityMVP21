package greencity.service;

import greencity.dto.event.EventImageDto;
import greencity.dto.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Provides the interface to manage {@code EventImage} entity.
 */
public interface EventImageService {

    /**
     * Uploads a list of images for a specific event.
     * Access is allowed only to the event creator or users with the ADMIN role.
     *
     * @param images  the list of image files to upload (max 5)
     * @param eventId the ID of the event to which the images belong
     * @param user    the current authenticated user performing the operation
     * @return a list of saved {@link EventImageDto} objects
     */
    List<EventImageDto> uploadEventImages(List<MultipartFile> images, Long eventId, UserVO user);
}