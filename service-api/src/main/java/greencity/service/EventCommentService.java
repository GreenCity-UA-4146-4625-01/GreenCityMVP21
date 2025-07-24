package greencity.service;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.user.UserVO;

public interface EventCommentService {
    /**
     * Creates a comment on an event.
     *
     * @param addEventCommentDtoRequest DTO with comment content and event ID
     * @param eventId     ID of the authenticated user
     * @return response DTO with created comment data
     */

    EventCommentDtoResponse createComment(AddEventCommentDtoRequest addEventCommentDtoRequest, Long eventId, UserVO userVO);
}
