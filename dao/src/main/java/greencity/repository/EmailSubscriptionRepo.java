package greencity.repository;

import greencity.entity.EmailSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface EmailSubscriptionRepo extends JpaRepository<EmailSubscription, UUID> {

    EmailSubscription findByEmail(String email);

    /**
     * Sets the given subscription's {@link EmailSubscription#lastSentEmailAt} to "now".
     *
     * @param subscriptionId The id of the subscription
     */
    @Transactional
    @Modifying
    @Query("update EmailSubscription s set s.lastSentEmailAt = current_timestamp where s.id = :subscriptionId")
    void updateSubscriptionLastSentEmailAt(UUID subscriptionId);

    @Query("select s from EmailSubscription s where ((:maxSentEmailAt < s.lastSentEmailAt) or (s.lastSentEmailAt is null)) order by s.lastSentEmailAt limit 5")
    List<EmailSubscription> findSubscriptionsWithPendingEmails(ZonedDateTime maxSentEmailAt);
}
