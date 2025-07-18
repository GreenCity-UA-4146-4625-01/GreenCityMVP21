package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Edit event by ID (accessible for ADMIN and OWNER only)")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = HttpStatuses.CREATED),
            @ApiResponse (responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse (responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse (responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EventResponseDto> editEvent(
            @PathVariable Long id,
            @RequestBody @Valid EditEventRequestDto editEventRequestDto,
            @AuthenticationPrincipal UserVO user
            ) {
        return ResponseEntity.ok(eventService.updateEventById(id, editEventRequestDto, user));
    }

    @Operation(summary = "Delete event by ID (accessible for ADMIN and OWNER only)")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse (responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse (responseCode = "403", description = HttpStatuses.FORBIDDEN),
            @ApiResponse (responseCode = "404", description = HttpStatuses.NOT_FOUND),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserVO user) {
        eventService.deleteEventById(id, user);
        return ResponseEntity.noContent().build();
    }
}
