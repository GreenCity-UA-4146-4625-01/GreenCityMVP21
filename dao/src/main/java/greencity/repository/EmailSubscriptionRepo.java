package greencity.repository;

import greencity.entity.EmailSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailSubscriptionRepo extends JpaRepository<EmailSubscription, UUID> {

    EmailSubscription findByEmail(String email);
}
