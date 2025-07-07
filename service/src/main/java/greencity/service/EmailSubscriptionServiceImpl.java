package greencity.service;

import greencity.dto.subscription.SubscriptionDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailSubscriptionServiceImpl implements EmailSubscriptionService {

    @Override
    public SubscriptionDto createSubscription(String email) {
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public void deleteSubscription(UUID subscriptionId) {
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public boolean shouldSendEmail(UUID subscriptionId) {
        throw new IllegalStateException("Not implemented yet");
    }
}
