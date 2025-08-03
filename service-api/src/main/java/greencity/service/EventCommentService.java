package greencity.service;
import greencity.dto.eventcomment.*;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventCommentService {
    /**
     * Creates a comment on an event.
     *
     * @param addEventCommentDtoRequest DTO with comment content and event ID
     * @param eventId     ID of the authenticated user
     * @return response DTO with created comment data
     */

    EventCommentDtoResponse createComment(AddEventCommentDtoRequest addEventCommentDtoRequest, Long eventId, UserVO userVO);

    Page<EventShortInfoUserVO> getMentionableUsers(String query, Pageable pageable);

    Page<EventCommentViewDto> getCommentsByEventId(Long eventId, int page, int size);

    EventCommentViewDto getCommentById(Long commentId);

    int countOfCommentsByEventId(Long eventId);

    void like(UserVO userVO, Long id);
}
