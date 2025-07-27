package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.EventImageDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing {@link EventImage} entities.
 */
@Service
@RequiredArgsConstructor
public class EventImageServiceImpl implements EventImageService {
    private final ModelMapper modelMapper;
    private final EventRepo eventRepo;
    private final FileService fileService;
    private final EventImageRepo eventImageRepo;

    /**
     * Uploads a list of images for a specific event.
     * <p>
     * Only one image will be marked as the main image — the first in the provided list.
     * The total number of images (existing + new) must not exceed 5 per event.
     * <p>
     * Access is allowed only to the event creator or users with the ADMIN role.
     *
     * @param images  the list of image files to upload (max 5)
     * @param eventId the ID of the event to which the images belong
     * @param user    the current authenticated user performing the operation
     * @return a list of saved {@link EventImageDto} objects
     * @throws NotFoundException                    if the event with the given ID does not exist
     * @throws BadRequestException                  if the total number of images exceeds the allowed limit
     * @throws UserHasNoPermissionToAccessException if the user is neither the event creator nor an admin
     */
    @Override
    public List<EventImageDto> uploadEventImages(@Validated
                                                 @Size(max = 5, message = ErrorMessage.MAX_EVENT_IMAGES_EXCEEDED)
                                                 List<MultipartFile> images,
                                                 Long eventId,
                                                 UserVO user) {
        Event event = getEventForOwnerAccess(eventId, user);

        List<EventImage> existingImages = eventImageRepo.findAllByEventId(eventId);

        validateImagesCount(existingImages, images);

        List<EventImage> allToSave = applyMainImageLogic(existingImages, images, event);
        List<EventImage> saved = eventImageRepo.saveAll(allToSave);

        return saved.stream()
                .map(img -> modelMapper.map(img, EventImageDto.class))
                .toList();
    }

    private void validateImagesCount(List<EventImage> existingImages, List<MultipartFile> newImages) {
        int totalImages = existingImages.size() + newImages.size();
        if (totalImages > 5) {
            throw new BadRequestException(ErrorMessage.MAX_EVENT_IMAGES_EXCEEDED);
        }
    }

    private List<EventImage> applyMainImageLogic(List<EventImage> existing, List<MultipartFile> newImages, Event event) {

        List<EventImage> eventImages = newImages.stream()
                .map(image -> EventImage.builder()
                        .url(fileService.upload(image))
                        .isMain(false)
                        .event(event)
                        .build())
                .toList();

        if (!eventImages.isEmpty()) {
            eventImages.getFirst().setIsMain(true);
        }
        List<EventImage> allImages = new ArrayList<>(existing);
        allImages.addAll(eventImages);

        return allImages;
    }

    @PostAuthorize("hasRole('ROLE_ADMIN') or #user.id == #returnObject.creator.id")
    private Event getEventForOwnerAccess (Long eventId, UserVO user) {
        return eventRepo.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
    }
}
