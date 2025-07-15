package greencity.controller;


import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    /**
     * Creates a new event in the system.
     * This endpoint accepts a valid {@link CreateEventRequestDto} containing event details,
     * creates the event, and returns the created event information.
     *
     * @param createEventRequestDto the DTO containing the data required to create a new event; must be valid
     * @return {@link ResponseEntity} with the created {@link EventResponseDto} and HTTP status 201 (Created)
     * @apiNote - Response code 201: Event successfully created.
     * - Response code 401: Unauthorized access (user not authenticated).
     * - Response code 403: Forbidden (user does not have permission to create an event).
     */
    @Operation(summary = "Create a new event.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)})
    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(
            @AuthenticationPrincipal UserVO user,
            @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(createEventRequestDto, user));
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
    @PatchMapping("/{id}")
    public ResponseEntity<EventResponseDto> editEvent(
            @PathVariable Long id,
            @RequestBody @Valid EditEventRequestDto editEventRequestDto,
            @AuthenticationPrincipal UserVO user
    ) {
        return ResponseEntity.ok(eventService.updateEventById(id, editEventRequestDto, user));
    }
}
