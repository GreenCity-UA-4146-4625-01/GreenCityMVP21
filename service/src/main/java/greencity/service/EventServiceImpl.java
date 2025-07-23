package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventDateTimeDto;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import greencity.validator.EventDateTimeDtoValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
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
        Event event = getEventEntityById(id);
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

    @Override
    @Transactional
    public EventResponseDto updateEventById(Long eventId, EditEventRequestDto dto, List<MultipartFile> images, UserVO user) {

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

        if (images != null && !images.isEmpty()) {
            List<EventImageDto> uploadedImages = eventImageService.uploadEventImages(images, dto.getEventId(), user);
            uploadedImages.stream()
                    .filter(EventImageDto::getIsMain)
                    .findFirst()
                    .ifPresent(mainImage -> event.setMainImageId(mainImage.getImageId()));
        }

        Event updatedEvent = modelMapper.map(dto, Event.class);

        Event saved = eventRepo.save(updatedEvent);

        return modelMapper.map(saved, EventResponseDto.class);
    }

    /**
     * Assigns a user to participate in an event by event ID.
     * <p>
     * This method adds the given user to the list of participants of the specified event.
     * If the user is already assigned, they will not be added again.
     * </p>
     *
     * @param eventId the ID of the event the user wants to join
     * @param user    the user information to be assigned to the event
     * @return {@link EventResponseDto} containing the updated event data
     * @throws NotFoundException if the event with the given ID does not exist
     */
    @Override
    public EventResponseDto assignUserToEvent(Long eventId, UserVO user) {
        Event event = getEventEntityById(eventId);
        User userEntity = modelMapper.map(user, User.class);

        event.getParticipants().add(userEntity);

        Event saved = eventRepo.save(event);
        return modelMapper.map(saved, EventResponseDto.class);
    }

    /**
     * Removes a user from the list of participants of the specified event.
     *
     * @param eventId the ID of the event to leave
     * @param user    the user who wants to leave the event
     * @return {@link EventResponseDto} containing the updated event data
     * @throws NotFoundException if the event with the given ID does not exist
     */
    @Override
    public EventResponseDto unassignUserFromEvent(Long eventId, UserVO user) {
        Event event = getEventEntityById(eventId);
        User userEntity = modelMapper.map(user, User.class);

        event.getParticipants().removeIf(participant -> participant.getId().equals(userEntity.getId()));

        Event saved = eventRepo.save(event);
        return modelMapper.map(saved, EventResponseDto.class);
    }

    private Event getEventEntityById(Long id) {
        return eventRepo.findEventById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
    }


    @PostAuthorize("hasRole('ROLE_ADMIN') or #user.id == #returnObject.creator.id")
    private Event getEventForOwnerAccess(Long eventId, UserVO user) {
        return eventRepo.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }
}
