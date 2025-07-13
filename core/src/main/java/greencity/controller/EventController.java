package greencity.controller;

import greencity.dto.event.EditEventRequestDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Edit event by ID (accessible for ADMIN and OWNER only)")
    @ApiResponses(value = {
            @ApiResponse (responseCode = "200", description = "Successfully edited event"),
            @ApiResponse (responseCode = "401", description = "Unauthorized - user is not authenticated"),
            @ApiResponse (responseCode = "403", description = "Forbidden - user is not the event owner or admin"),
            @ApiResponse (responseCode = "404", description = "Event not found"),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<EditEventRequestDto> editEvent(
            @PathVariable Long id,
            @RequestBody @Valid EditEventRequestDto editEventRequestDto,
            @AuthenticationPrincipal UserVO user
            ) {
        EditEventRequestDto updated = eventService.updateEventById(id, editEventRequestDto, user);
        return ResponseEntity.ok(updated);
    }

}
