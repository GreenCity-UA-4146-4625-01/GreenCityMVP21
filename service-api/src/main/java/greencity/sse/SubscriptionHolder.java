package greencity.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class SubscriptionHolder<T> {

    private final Set<StreamingSubscription<T>> subscriptions = ConcurrentHashMap.newKeySet();

    public StreamingSubscription<T> createForUser(Long userId) {
        StreamingSubscription<T> subscription = new StreamingSubscription<>(this, userId);
        subscriptions.add(subscription);
        return subscription;
    }

    public void notifyByUser(Long userId, T data) {
        subscriptions.stream()
                .filter(subscription -> subscription.getUserId().equals(userId))
                .forEach(subscription -> subscription.notify(data));
    }

    public void removeSubscription(StreamingSubscription<T> subscription) {
        subscriptions.remove(subscription);
    }
}
