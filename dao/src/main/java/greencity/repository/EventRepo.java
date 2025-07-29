package greencity.repository;

import greencity.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EventRepo extends JpaRepository<Event, Long> {

    Page<Event> findByTitleContainsIgnoreCase(String fragment, Pageable pageable);

    Optional<Event> findEventById(Long id);

    Page<Event> findByParticipants_Id(Long id,
                                      Pageable pageable);

    @Query("select e.creator.id from Event e where e.id = ?1")
    Long getCreatorIdByEventId(Long id);

    @Override
    void deleteById(Long aLong);
}
