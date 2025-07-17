package greencity.service;

import greencity.dto.econews.EcoNewsForDigestDto;
import greencity.dto.subscription.SubscriptionDto;

import java.util.List;
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
     * Retrieve the next batch of EcoNews to send in an email for that subscription.
     * If this subscription is very recently created and didn't have any emails sent yet, returns the most recent.
     *
     * @param subscriptionId The subscription id as returned by {@link EmailSubscriptionService#createSubscription(String)}.
     * @return List of EcoNews to be sent in the next email
     */
    List<EcoNewsForDigestDto> getNewsForNextEmail(UUID subscriptionId);

    /**
     * Checks if enough time has passed to send the next email for this subscription.
     *
     * @param subscriptionId The subscription id
     * @return Whether enough time has passed
     */
    boolean shouldSendNewEmail(UUID subscriptionId);

    /**
     * Sends an email for this subscription if there's anything to send.
     *
     * @param subscriptionId The subscription id
     */
    void sendEmail(UUID subscriptionId);
}
