package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.eventcomment.*;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/events")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @Operation(summary = "add comment")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED,
                    content = @Content(schema = @Schema(implementation = EventCommentDtoResponse.class))),
            @ApiResponse(responseCode = "303", description = HttpStatuses.SEE_OTHER),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })  
    @PostMapping("/{eventId}/comments")
    public ResponseEntity<EventCommentDtoResponse> addComment(@PathVariable Long eventId,
        @Valid @RequestBody AddEventCommentDtoRequest request,
        @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventCommentService.createComment(request, eventId, userVO));
    }

    @Operation(summary = "get mentioned-users")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/comments/mentioned-users")
    public ResponseEntity<Page<EventShortInfoUserVO>> autocompleteMentionedUsers(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.getMentionableUsers(query, pageable));
    }

    @Operation(summary = "get comments")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{eventId}/comments")
    public ResponseEntity<Page<EventCommentViewDto>> getComments(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.getCommentsByEventId(eventId, page, size));
    }

    @Operation(summary = "get comment by id")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/comments/{commentId}")
    public ResponseEntity<EventCommentViewDto> getComment(@PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.getCommentById(commentId));
    }

    @Operation(summary = "get count of comments")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/{eventId}/comments/count")
    public int getCommentsCountByEventId(@PathVariable Long eventId) {
        return eventCommentService.countOfCommentsByEventId(eventId);
    }

    @Operation(summary = "Like comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/comments/like")
    public void like(@RequestParam("id") Long id, @Parameter(hidden = true) @CurrentUser UserVO user) {
        eventCommentService.like(user, id);
    }

    /**
     * Edits an existing comment identified by {@code commentId}.
     *
     * @param commentId the ID of the comment to be edited
     * @param currentUser the currently authenticated user (injected by security context)
     * @param request the request body containing updated comment data (text and mentioned users)
     * @return a {@link ResponseEntity} containing the updated comment DTO {@link EventCommentEditViewDto}
     *         with HTTP status 200 (OK) if successful
     */
    @Operation(summary = "Edit comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<EventCommentEditViewDto> editComment(@PathVariable("commentId") Long commentId, @Parameter(hidden = true)@CurrentUser UserVO currentUser,
                                                           @Valid @RequestBody EditEventCommentDtoRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(eventCommentService.editComment(commentId, currentUser, request));
    }

}
