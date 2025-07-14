package greencity.repository;


import greencity.entity.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventImageRepo extends JpaRepository<EventImage, Long> {
    List<EventImage> findAllByEventId(Long eventId);
}
