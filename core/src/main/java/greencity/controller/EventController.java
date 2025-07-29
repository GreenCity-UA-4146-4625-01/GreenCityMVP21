package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.annotations.ValidImage;
import greencity.constant.ErrorMessage;
import greencity.constant.HttpStatuses;
import greencity.constant.ValidationConstants;
import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventPreviewDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * Creates a new event.
     * <p>
     * Accepts multipart/form-data with:
     * - JSON part: {@link CreateEventRequestDto}
     * - Optional images: list of {@link MultipartFile}, first one is main
     * <p>
     * Requires authenticated user via {@link CurrentUser}.
     *
     * @param user                  authenticated user
     * @param createEventRequestDto event details
     * @param images                optional image files
     * @return created {@link EventResponseDto} with status 201
     */
    @Operation(summary = "Create a new event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)})
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponseDto> createEvent(
            @CurrentUser UserVO user,
            @RequestPart("event") @Valid CreateEventRequestDto createEventRequestDto,
            @RequestPart(value = "images", required = false) @ValidImage @Size(max = 5, message = ErrorMessage.MAX_EVENT_IMAGES_EXCEEDED) List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.createEvent(createEventRequestDto, user, images));
    }

    /**
     * Retrieves an event by its unique identifier.
     * This endpoint fetches the details of a single event specified by its ID.
     *
     * @param id the unique identifier of the event to retrieve
     * @return {@link ResponseEntity} containing the {@link EventResponseDto} with HTTP status 200 (OK)
     * if the event is found; otherwise, HTTP status 404 (Not Found) if no event exists with the given ID.
     * @apiNote - Response code 200: Event successfully retrieved.
     * - Response code 404: Event with the specified ID not found.
     */
    @Operation(summary = "Search for an event by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)})
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    /**
     * Retrieves a paginated list of all events, optionally sorted by creation date in descending order by default.
     * This endpoint allows clients to fetch events with pagination and sorting support.
     * The results are returned in a {@link PageableDto} wrapper containing event data and pagination metadata.
     *
     * @param pageable pagination and sorting information, by default sorted by "creationDate" descending
     * @return {@link ResponseEntity} containing {@link PageableDto} of {@link EventResponseDto} with HTTP status 200 (OK)
     * if events are found; if no events are found, the response will still be 200 with an empty list.
     * @apiNote - Response code 200: Events successfully retrieved.
     * - Response code 404: No events found.
     */
    @Operation(summary = "Search all events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK)})
    @GetMapping
    public ResponseEntity<PageableDto<EventResponseDto>> getAllEvents(
            @PageableDefault(sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @Operation(summary = "Edit event by ID (accessible for ADMIN and OWNER only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponseDto> editEvent(
            @PathVariable Long id,
            @RequestPart("event") @Valid EditEventRequestDto editEventRequestDto,
            @RequestPart(value = "images", required = false) @ValidImage List<MultipartFile> images,
            @CurrentUser UserVO user
    ) {
        return ResponseEntity.ok(eventService.updateEventById(id, editEventRequestDto, images, user));
    }


    /**
     * Assigns the current user to the specified event.
     * <p>
     * Adds the authenticated user to the list of participants for the given event.
     * Requires the user to be authenticated via {@link CurrentUser}.
     *
     * @param eventId ID of the event to join
     * @param user    authenticated user
     * @return updated {@link EventResponseDto} with status 200
     * @apiNote - Response code 200: User successfully joined the event
     * - Response code 404: Event not found
     */
    @Operation(summary = "Join an event (assign current user)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{eventId}/join")
    public ResponseEntity<EventResponseDto> joinEvent(
            @PathVariable Long eventId,
            @Parameter(hidden = true) @CurrentUser UserVO user
    ) {
        return ResponseEntity.ok(eventService.assignUserToEvent(eventId, user));
    }

    /**
     * Removes the current user from the specified event.
     * <p>
     * Unassigns the authenticated user from the list of event participants.
     * Requires the user to be authenticated via {@link CurrentUser}.
     *
     * @param eventId ID of the event to leave
     * @param user    authenticated user
     * @return updated {@link EventResponseDto} with status 200
     * @apiNote - Response code 200: User successfully left the event
     * - Response code 404: Event not found
     */
    @Operation(summary = "Leave an event (unassign current user)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{eventId}/leave")
    public ResponseEntity<EventResponseDto> leaveEvent(
            @PathVariable Long eventId,
            @Parameter(hidden = true) @CurrentUser UserVO user
    ) {
        return ResponseEntity.ok(eventService.unassignUserFromEvent(eventId, user));
    }

    /**
     * Retrieves a paginated list of events that the currently authenticated user is assigned to.
     * <p>
     * This endpoint allows the user to fetch all events they are involved in as an assignee.
     * The results are sorted by creation date in descending order by default.
     * Requires authentication.
     *
     * @param pageable the pagination and sorting information (e.g., page number, size, sort order)
     * @param user     the currently authenticated user (injected automatically)
     * @return a pageable list of {@link EventResponseDto} representing assigned events
     */
    @Operation(
            summary = "Get all events assigned to the current user",
            description = "Returns a paginated list of events that the currently authenticated user has been assigned to. " +
                    "Requires user to be authenticated."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/assigned")
    public ResponseEntity<PageableDto<EventResponseDto>> getEventsAssignedToUser(
            @PageableDefault(sort = "creationDate", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(hidden = true) @CurrentUser UserVO user
    ) {
        return ResponseEntity.ok(eventService.getEventsAssignedToUser(user, pageable));
    }

    @Operation(summary = "Delete event by ID (accessible for ADMIN and OWNER only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @CurrentUser UserVO user) {
        eventService.deleteEventById(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for events by a given keyword present in the event title.
     * <p>
     * This endpoint allows users to search for events using partial or full keywords. The search is case-insensitive
     * and is performed only across event titles. The input must be between 3 and 64 characters in length.
     * <p>
     * Returns a list of {@link EventPreviewDto} sorted by textual relevance to the search query.
     *
     * @param query the keyword to search within event titles; must be 3–64 characters long
     * @return a list of matching {@link EventPreviewDto} sorted by relevance; an empty list if no matches found
     */
    @Operation(
            summary = "Search events by title",
            description = "Allows users to search for events using a keyword that matches (partially or fully) the event title. " +
                    "The search is case-insensitive and returns a list of matching events sorted by relevance."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<EventPreviewDto>> searchEvents(
            @RequestParam
            @Size(min = 3, max = 64, message = ValidationConstants.SEARCH_TEXT_VALIDATION_MESSAGE)
            String query) {
        return ResponseEntity.ok(eventService.searchEventsByTitle(query));
    }
}
