package greencity.repository;

import greencity.entity.Notification;
import greencity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiver(User receiver);

    @Query("SELECT n FROM Notification n WHERE n.receiver = :receiver AND NOT n.isRead")
    List<Notification> findUnreadByReceiver(User receiver);
}
