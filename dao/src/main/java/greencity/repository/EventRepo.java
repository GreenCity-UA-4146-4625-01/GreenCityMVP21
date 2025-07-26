package greencity.repository;

import greencity.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface EventRepo extends JpaRepository<Event, Long> {

    Optional<Event> findEventById(Long id);

    @Query("select e.creator.id from Event e where e.id = ?1")
    Long getCreatorIdByEventId(Long id);

    @Override
    void deleteById(Long aLong);


}
