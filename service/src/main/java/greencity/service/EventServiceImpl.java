package greencity.service;

import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import greencity.validator.EventDateTimeDtoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing {@link Event} entities.
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final ModelMapper modelMapper;
    private final EventRepo eventRepo;
    private final FileService fileService;
    private final EventDateTimeDtoValidator eventDateTimeDtoValidator;
    private final EventImageRepo eventImageRepo;

    /**
     * Creates a new event based on the provided {@link CreateEventRequestDto}.
     *
     * @param createEventRequestDto the DTO containing data to create the event; must not be {@code null}
     * @return the {@link EventResponseDto} representing the newly created event
     */
    @Override
    public EventResponseDto createEvent(CreateEventRequestDto createEventRequestDto) {
        Event event = modelMapper.map(createEventRequestDto, Event.class);
        Event saved = eventRepo.save(event);
        return modelMapper.map(saved, EventResponseDto.class);
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the unique identifier of the event; must not be {@code null}
     * @return the {@link EventResponseDto} representing the found event
     * @throws NotFoundException if no event with the given id exists
     */
    @Override
    public EventResponseDto getEventById(Long id) {
        Event event = eventRepo.findById(id).orElseThrow(
                () -> new NotFoundException("Event not found"));
        return modelMapper.map(event, EventResponseDto.class);
    }

    /**
     * Retrieves all events available in the system.
     *
     * @return a list of {@link EventResponseDto} objects; the list may be empty if no events exist
     */
    @Override
    public List<EventResponseDto> getAllEvents() {
        List<Event> events = eventRepo.findAll();
        return events.stream()
                .map(event -> modelMapper.map(event, EventResponseDto.class))
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDto updateEventById(Long eventId, EditEventRequestDto dto, UserVO user) {

        Event event = eventRepo.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !event.getCreator().getId().equals(user.getId())) {
            throw new UserHasNoPermissionToAccessException("You are bot allowed to edit this event");
        }

        if (dto.getEventDateTimes() != null) {

            for (EventDateTimeDto dateTimeDto : dto.getEventDateTimes()) {
                eventDateTimeDtoValidator.validateAndFill(dateTimeDto);
            }
        }

        Event updatedEvent = modelMapper.map(dto, Event.class);

        Event saved = eventRepo.save(updatedEvent);

        return modelMapper.map(saved, EventResponseDto.class);
    }
  
    /**
     * Uploads a single image for a specific event.
     * <p>
     * The image can optionally be marked as main, but only one main image is allowed per event.
     * The total number of images per event cannot exceed 5.
     *
     * @param dto      the {@link UploadEventImageDto} containing the image and its metadata
     * @param eventId  the ID of the event to associate the image with
     * @return the {@link EventImageDto} representing the uploaded image
     * @throws NotFoundException     if the event with the given ID is not found
     * @throws BadRequestException  if the image limit is exceeded or a main image already exists
     */
    @Override
    public EventImageDto uploadEventImage(@Valid UploadEventImageDto dto, Long eventId) {
        Event event = getEventOrThrow(eventId);
        List<EventImage> existingImages = eventImageRepo.findAllByEventId(eventId);

        validateImagesCountAndMain(existingImages, List.of(dto));
        return saveEventImage(dto, event);
    }

    /**
     * Uploads a list of images for a specific event.
     * <p>
     * Each image can be marked as main, but only one main image is allowed per event.
     * The total number of images per event cannot exceed 5.
     *
     * @param imagesDto the {@link UploadEventImagesDto} containing a list of image upload data
     * @param eventId    the ID of the event to associate images with
     * @return a list of {@link EventImageDto} representing the uploaded images
     * @throws NotFoundException     if the event with the given ID is not found
     * @throws BadRequestException  if the image limit is exceeded or more than one main image is submitted
     */
    @Override
    public List<EventImageDto> uploadEventImages(@Valid UploadEventImagesDto imagesDto, Long eventId) {
        Event event = getEventOrThrow(eventId);
        List<UploadEventImageDto> dtos = imagesDto.getImages();

        List<EventImage> existingImages = eventImageRepo.findAllByEventId(eventId);
        validateImagesCountAndMain(existingImages, dtos);

        return dtos.stream()
                .map(dto -> saveEventImage(dto, event))
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

        boolean hasExistingMain = existingImages.stream().anyMatch(EventImage::getIsMain);
        long newMainCount = newImages.stream().filter(img -> Boolean.TRUE.equals(img.getIsMainImage())).count();

        if (hasExistingMain && newMainCount > 0) {
            throw new BadRequestException("Only one main image is allowed.");
        }

        if (!hasExistingMain && newMainCount > 1) {
            throw new BadRequestException("Only one main image is allowed.");
        }
    }

    private EventImageDto saveEventImage(UploadEventImageDto dto, Event event) {
        String url = fileService.upload(dto.getImage());
        EventImage eventImage = EventImage.builder()
                .url(url)
                .isMain(dto.getIsMainImage())
                .event(event)
                .build();
        EventImage saved = eventImageRepo.save(eventImage);
        return modelMapper.map(saved, EventImageDto.class);
    }
}
