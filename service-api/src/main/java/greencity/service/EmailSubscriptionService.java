package greencity.service;

import greencity.dto.subscription.SubscriptionDto;

import java.util.UUID;

public interface EmailSubscriptionService {
    /**
     * Subscribe to emails from GreenCity.
     * @param email The email address that will get messages.
     * @return DTO with the subscription id, used for unsubscribing.
     */
    SubscriptionDto createSubscription(String email);

    /**
     * Unsubscribe from emails from GreenCity.
     * @param subscriptionId The subscription id as returned by {@link EmailSubscriptionService#createSubscription(String)}.
     * @throws greencity.exception.exceptions.NotFoundException Throws if no such subscription exists.
     */
    void deleteSubscription(UUID subscriptionId);

    /**
     * Check if we should send the next e-mail for this subscription, e.g. enough time has passed and there's something
     * to send.
     * @param subscriptionId The subscription id as returned by {@link EmailSubscriptionService#createSubscription(String)}.
     * @return Whether a new email should be sent soon
     */
    boolean shouldSendEmail(UUID subscriptionId);
}
