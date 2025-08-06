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
import greencity.listeners.EventCancellationEvent;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import greencity.validator.EventDateTimeDtoValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private final EventImageRepo eventImageRepo;
    private final FileService fileService;
    private final ApplicationEventPublisher eventPublisher;

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

    @Transactional
    @Override
    public EventResponseDto updateEventById(Long eventId, EditEventRequestDto dto, List<MultipartFile> images, UserVO user) {

        Event event = eventRepo.findEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !event.getCreator().getId().equals(user.getId())) {
            throw new UserHasNoPermissionToAccessException("You are not allowed to edit this event");
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getVisibility() != null) {
            event.setEventVisibility(dto.getVisibility());
        }

        if (dto.getEventTypes() != null) {
            event.setEventTypes(dto.getEventTypes());
        }

        if (dto.getLocations() != null) {
            event.getEventLocations().clear();
            dto.getLocations().forEach(locationDto -> {
                EventLocation location = modelMapper.map(locationDto, EventLocation.class);
                location.setEvent(event);
                event.getEventLocations().add(location);
            });
        }

        if (dto.getEventDateTimes() != null) {
            event.getEventDateTimes().clear();
            dto.getEventDateTimes().forEach(dateTimeDto -> {
                eventDateTimeDtoValidator.validateAndFill(dateTimeDto);
                EventDateTime eventDateTime = modelMapper.map(dateTimeDto, EventDateTime.class);
                eventDateTime.setEvent(event);
                event.getEventDateTimes().add(eventDateTime);
            });
        }

        if (dto.getOnlineLinks() != null) {
            event.getOnlineLinks().clear();
            dto.getOnlineLinks().forEach(link -> event.getOnlineLinks().add(link));
        }

        if (images != null && !images.isEmpty()) {
            List<EventImageDto> uploadedImages = eventImageService.uploadEventImages(images, eventId, user);
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
        if (!eventRepo.existsById(eventId)) {
            throw new NotFoundException("Event not found");
        }

        Long creatorId = eventRepo.getCreatorIdByEventId(eventId);
        if (creatorId == null) {
            throw new NotFoundException("Creator Id not found");
        }

        if (!user.getRole().equals(Role.ROLE_ADMIN) && !creatorId.equals(user.getId())) {
            throw new UserHasNoPermissionToAccessException("You are not allowed to delete this event");
        }

        eventPublisher.publishEvent(new EventCancellationEvent(this,
                eventId,
                eventRepo.getReferenceById(eventId).getTitle(),
                eventRepo.getReferenceById(eventId).getParticipants()));

        eventRepo.deleteById(eventId);
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
        Event event = getEventForOwnerAccess(eventId, user);
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
        Event event = getEventForOwnerAccess(eventId, user);
        User userEntity = modelMapper.map(user, User.class);

        event.getParticipants().removeIf(participant -> participant.getId().equals(userEntity.getId()));

        Event saved = eventRepo.save(event);
        return modelMapper.map(saved, EventResponseDto.class);
    }

    /**
     * Retrieves a paginated list of events that the given user has joined (i.e., is assigned to as a participant).
     * <p>
     * The method queries the {@code Event} entities where the user is listed as a participant and maps them to
     * {@link EventResponseDto} objects. The result is wrapped in a {@link PageableDto} with pagination metadata.
     *
     * @param user     the currently authenticated user whose assigned events should be fetched; must not be {@code null}
     * @param pageable the pagination and sorting information
     * @return a {@link PageableDto} containing a list of {@link EventResponseDto} that the user is assigned to;
     * the list may be empty if the user is not participating in any events
     */
    @Override
    public PageableDto<EventResponseDto> getEventsAssignedToUser(UserVO user, Pageable pageable) {
        Page<Event> byParticipantsId = eventRepo.findByParticipants_Id(user.getId(), pageable);

        List<EventResponseDto> content = byParticipantsId.stream()
                .map(event -> modelMapper.map(event, EventResponseDto.class))
                .toList();

        return new PageableDto<>(
                content,
                byParticipantsId.getTotalElements(),
                byParticipantsId.getNumber(),
                byParticipantsId.getTotalPages());
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

    /**
     * Searches for events based on a provided keyword fragment in the event title.
     * <p>
     * The method performs a case-insensitive search across event titles, returning a list of events whose titles
     * contain the specified query string. The results are sorted by textual relevance to the search query
     * (i.e., the better the match in the title, the higher the event is placed in the list).
     * Each matching {@code Event} entity is mapped to an {@link EventPreviewDto} for client display.
     *
     * @param query the search keyword to look for in event titles; must be between 3 and 64 characters long
     * @return a list of {@link EventPreviewDto} that match the search query, sorted by relevance; may be empty if no matches are found
     */
    @Override
    public PageableDto<EventPreviewDto> searchEventsByTitle(String query, Pageable pageable) {
        String normolizedQuery = query.toLowerCase();

        List<EventPreviewDto> sorted = eventRepo.findByTitleContainsIgnoreCase(query, pageable).stream()
                .sorted(Comparator.comparingInt((Event event) -> {
                            String normalizedTitle = event.getTitle().toLowerCase();
                            return calculateRelevance(normalizedTitle, normolizedQuery);
                        })
                        .reversed()
                )
                .map(event -> modelMapper.map(event, EventPreviewDto.class))
                .collect(Collectors.toList());

        int total = sorted.size();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<EventPreviewDto> pageContent = fromIndex >= total ? List.of() : sorted.subList(fromIndex, toIndex);
        return new PageableDto<>(
                pageContent,
                total,
                pageNumber,
                (int) Math.ceil((double) total / pageSize)
        );
    }

    private int calculateRelevance(String normalizedTitle, String normolizedQuery) {
        if (normalizedTitle.equals(normolizedQuery)) return 3;
        if (normalizedTitle.startsWith(normolizedQuery)) return 2;
        if (normalizedTitle.contains(normolizedQuery)) return 1;
        return 0;
    }
  
     /** Updates the location of the event identified by the given event ID.
     * <p>
     * Only the event OWNER or an ADMIN user is authorized to perform this operation.
     * The existing event locations are cleared and replaced with the new location provided.
     * The bidirectional relationship between {@link Event} and {@link EventLocation} is maintained by setting the event reference in the new location.
     *
     * @param eventId           the ID of the event to update; must not be {@code null}
     * @param eventLocationDto  the new location data transfer object; must not be {@code null} and valid
     * @param user              the user performing the update operation; must not be {@code null}
     * @return the updated {@link EventResponseDto} reflecting the new location
     * @throws NotFoundException                    if no event exists with the given ID
     * @throws UserHasNoPermissionToAccessException if the user is not the OWNER or ADMIN
     */
    @Override
    public EventResponseDto updateLocationByEventId(Long eventId, EventLocationDto eventLocationDto, UserVO user) {
        Event event = getEventForOwnerAccess(eventId, user);

        EventLocation eventLocation = modelMapper.map(eventLocationDto, EventLocation.class);
        eventLocation.setEvent(event);

        event.getEventLocations().clear();

        event.getEventLocations().add(eventLocation);

        Event saved = eventRepo.save(event);

        return modelMapper.map(saved, EventResponseDto.class);
    }
}
