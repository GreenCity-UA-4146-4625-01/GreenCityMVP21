package greencity.repository;

import greencity.dto.eventcomment.EventCommentEditViewDto;
import greencity.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    @Query("SELECT c FROM EventComment c WHERE c.event.id = :eventId AND c.parentComment IS NULL AND c.deleted = false ORDER BY c.modifiedDate DESC")
    Page<EventComment> findTopLevelCommentsByEventId(Long eventId, Pageable pageable);

    @Query("SELECT count(ec) FROM EventComment ec "
            + "WHERE ec.parentComment IS NULL AND ec.event.id = ?1 AND ec.deleted = FALSE")
    int countOfComments(Long eventId);

    /**
     * Retrieves an {@link EventCommentEditViewDto} projection of an EventComment by its unique ID.
     * <p>
     * The query selects specific fields from the EventComment entity and maps them into the DTO:
     * <ul>
     *   <li>Comment ID</li>
     *   <li>User's name</li>
     *   <li>User's profile picture path</li>
     *   <li>Created date of the comment</li>
     *   <li>Last modified date of the comment</li>
     *   <li>Number of users who liked the comment</li>
     *   <li>Text content of the comment</li>
     * </ul>
     *
     * @param commentId the unique identifier of the EventComment entity
     * @return an {@link EventCommentEditViewDto} containing selected comment details,
     *         or {@code null} if no comment with the given ID exists
     */
    @Query("""
            SELECT new greencity.dto.eventcomment.EventCommentEditViewDto(
            cm.id,
            cm.user.name,
            cm.user.profilePicturePath,
            cm.createdDate,
            cm.modifiedDate,
            SIZE(cm.usersLiked),
            cm.text
            )
            FROM EventComment cm\s
            WHERE cm.id = :commentId\s
            """)
    EventCommentEditViewDto getEventCommentByID(@Param("commentId") Long commentId);
}
