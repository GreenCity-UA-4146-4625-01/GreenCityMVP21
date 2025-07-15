package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.UploadEventImageDto;
import greencity.dto.event.UploadEventImagesDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
    @Override
    public EventImageDto uploadEventImage(@Valid UploadEventImageDto dto, Long eventId, UserVO user) {
        Event event = getEventOrThrow(eventId);

        checkUserPermission(event, user);

        List<UploadEventImageDto> newDtos = List.of(dto);
        List<EventImage> existing = eventImageRepo.findAllByEventId(eventId);

        validateImagesCountAndMain(existing, newDtos);

        List<EventImage> allToSave = applyMainImageLogic(existing, newDtos, event);
        List<EventImage> saved = eventImageRepo.saveAll(allToSave);

        return modelMapper.map(saved.getLast(), EventImageDto.class);
    }

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
    @Override
    public List<EventImageDto> uploadEventImages(@Valid UploadEventImagesDto imagesDto, Long eventId, UserVO user) {
        Event event = getEventOrThrow(eventId);

        checkUserPermission(event, user);

        List<UploadEventImageDto> dtos = imagesDto.getImages();
        List<EventImage> existingImages = eventImageRepo.findAllByEventId(eventId);

        validateImagesCountAndMain(existingImages, dtos);

        List<EventImage> allToSave = applyMainImageLogic(existingImages, dtos, event);
        List<EventImage> saved = eventImageRepo.saveAll(allToSave);

        return saved.stream()
                .map(img -> modelMapper.map(img, EventImageDto.class))
                .toList();
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    private void validateImagesCountAndMain(List<EventImage> existingImages, List<UploadEventImageDto> newImages) {
        int totalImages = existingImages.size() + newImages.size();
        if (totalImages > 5) {
            throw new BadRequestException("Maximum of 5 images allowed per event.");
        }

        long newMainCount = newImages.stream().filter(img -> Boolean.TRUE.equals(img.getIsMainImage())).count();

        long totalMainCount = existingImages.stream().filter(EventImage::getIsMain).count() + newMainCount;
        if (totalMainCount > 1) {
            throw new BadRequestException("Only one main image is allowed.");
        }
    }

    private void checkUserPermission(Event event, UserVO user) {
        if (!user.getRole().equals(Role.ROLE_ADMIN) && !event.getCreator().getId().equals(user.getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
    }

    private List<EventImage> applyMainImageLogic(List<EventImage> existing, List<UploadEventImageDto> newDtos, Event event) {
        long mainCount = newDtos.stream()
                .filter(dto -> Boolean.TRUE.equals(dto.getIsMainImage()))
                .count();

        if (mainCount > 1) {
            throw new BadRequestException("Only one image can be marked as main.");
        }

        if (mainCount > 0) {
            existing.forEach(img -> img.setIsMain(false));
        }

        List<EventImage> newImages = newDtos.stream()
                .map(dto -> EventImage.builder()
                        .url(fileService.upload(dto.getImage()))
                        .isMain(dto.getIsMainImage())
                        .event(event)
                        .build())
                .toList();

        List<EventImage> allImages = new ArrayList<>(existing);
        allImages.addAll(newImages);

        return allImages;
    }
}
