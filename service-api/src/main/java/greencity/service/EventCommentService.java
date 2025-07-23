package greencity.service;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;

public interface EventCommentService {
    /**
     * Creates a comment on an event.
     *
     * @param addEventCommentDtoRequest DTO with comment content and event ID
     * @param userId     ID of the authenticated user
     * @return response DTO with created comment data
     */

    EventCommentDtoResponse createComment(AddEventCommentDtoRequest addEventCommentDtoRequest, Long userId);
}
