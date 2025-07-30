package greencity.repository;

import greencity.entity.EventComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EventCommentRepository extends JpaRepository<EventComment, Long> {
    @Query("SELECT c FROM EventComment c WHERE c.event.id = :eventId AND c.parentComment IS NULL AND c.deleted = false ORDER BY c.modifiedDate DESC")
    Page<EventComment> findTopLevelCommentsByEventId(Long eventId, Pageable pageable);
}
