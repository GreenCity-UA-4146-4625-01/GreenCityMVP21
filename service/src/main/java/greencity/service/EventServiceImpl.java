package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventDateTime;
import greencity.entity.EventLocation;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import greencity.validator.EventDateTimeDtoValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service implementation for managing {@link Event} entities.
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final ModelMapper modelMapper;
    private final EventRepo eventRepo;
    private final EventDateTimeDtoValidator eventDateTimeDtoValidator;
    private final EventImageService eventImageService;

    /**
     * Creates a new event based on the provided {@link CreateEventRequestDto}.
     *
     * @param createEventRequestDto the DTO containing data to create the event; must not be {@code null}
     * @return the {@link EventResponseDto} representing the newly created event
     */
    @Transactional
    @Override
    public EventResponseDto createEvent(CreateEventRequestDto createEventRequestDto, UserVO user, List<MultipartFile> images) {
        Event event = modelMapper.map(createEventRequestDto, Event.class);
        event.setCreator(modelMapper.map(user, User.class));
        Event saved = eventRepo.save(event);

        if (images != null && !images.isEmpty()) {
            List<EventImageDto> eventImageDtos = eventImageService.uploadEventImages(
                    images, saved.getId(), user);

            eventImageDtos.stream()
                    .filter(EventImageDto::getIsMain)
                    .findFirst()
                    .ifPresent(mainImage -> {
                        saved.setMainImageId(mainImage.getImageId());
                        eventRepo.save(saved);
                    });
        }

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
     * Retrieves a paginated list of all events available in the system.
     * This method fetches events from the repository according to the provided pagination
     * information, converts each {@link Event} entity to an {@link EventResponseDto},
     * and wraps the result along with pagination metadata into a {@link PageableDto}.
     *
     * @param pageable the pagination information (page number, size, sorting)
     * @return a {@link PageableDto} containing a list of {@link EventResponseDto} objects for the requested page,
     * along with pagination details such as total elements, current page number, and total pages.
     * The list may be empty if no events exist for the given page.
     */
    @Override
    public PageableDto<EventResponseDto> getAllEvents(Pageable pageable) {
        Page<Event> eventPage = eventRepo.findAll(pageable);

        List<EventResponseDto> content = eventPage.stream()
                .map(event -> modelMapper.map(event, EventResponseDto.class))
                .toList();

        return new PageableDto<>(
                content,
                eventPage.getTotalElements(),
                eventPage.getNumber(),
                eventPage.getTotalPages()
        );
    }

    @Transactional
    @Override
    public EventResponseDto updateEventById(Long eventId, EditEventRequestDto dto, List<MultipartFile> images, UserVO user) {
        Event event = eventRepo.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !event.getCreator().getId().equals(user.getId())) {
            throw new UserHasNoPermissionToAccessException("You are not allowed to edit this event");
        }

        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventVisibility(dto.getVisibility());

        if (dto.getEventTypes() != null) {
            event.setEventTypes(dto.getEventTypes());
        }

        // Оновлення locations
        if (dto.getLocations() != null) {
            event.getEventLocations().clear();
            dto.getLocations().forEach(locationDto -> {
                EventLocation location = modelMapper.map(locationDto, EventLocation.class);
                location.setEvent(event);
                event.getEventLocations().add(location);
            });
        }

        // Оновлення eventDateTimes (з валідацією)
        if (dto.getEventDateTimes() != null) {
            event.getEventDateTimes().clear();
            dto.getEventDateTimes().forEach(dateTimeDto -> {
                eventDateTimeDtoValidator.validateAndFill(dateTimeDto);
                EventDateTime eventDateTime = modelMapper.map(dateTimeDto, EventDateTime.class);
                eventDateTime.setEvent(event);
                event.getEventDateTimes().add(eventDateTime);
            });
        }

        // Оновлення onlineLinks
        if (dto.getOnlineLinks() != null) {
            event.getOnlineLinks().clear();
            dto.getOnlineLinks().forEach(link -> event.getOnlineLinks().add(link));
        }

        if (images != null && !images.isEmpty()) {
            List<EventImageDto> uploadedImages = eventImageService.uploadEventImages(images, dto.getEventId(), user);
            uploadedImages.stream()
                    .filter(EventImageDto::getIsMain)
                    .findFirst()
                    .ifPresent(mainImage -> event.setMainImageId(mainImage.getImageId()));
        }

        Event updatedEvent = eventRepo.save(event);
        return modelMapper.map(updatedEvent, EventResponseDto.class);
    }


    @Override
    @Transactional
    public void deleteEventById(Long eventId, UserVO user) {
        Event event = eventRepo.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !event.getCreator().getId().equals(user.getId())) {
            throw new UserHasNoPermissionToAccessException("You are bot allowed to delete this event");
        }

        event.getEventDateTimes().size();
        event.getEventImages().size();

        eventRepo.delete(event);
    }
}
