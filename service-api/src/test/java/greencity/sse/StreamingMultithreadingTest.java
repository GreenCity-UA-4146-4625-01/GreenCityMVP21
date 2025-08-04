package greencity.sse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StreamingMultithreadingTest {

    @Spy
    private final SubscriptionHolder<String> subscriptions = new SubscriptionHolder<>();

    private static final Long USER_ID = 1L;
    private static final Long USER_ID_2 = 2L;

    private static final String DATA_1 = "test-data-1";
    private static final String DATA_2 = "test-data-2";

    @Test
    void subscription_shouldBeCreatedWithCorrectUserId() {
        try (var subscription = subscriptions.createForUser(USER_ID)) {
            assertEquals(USER_ID, subscription.getUserId());
        }
    }

    @Test
    void subscription_shouldNotBeClosedWhenCreated() {
        try (var subscription = subscriptions.createForUser(USER_ID)) {
            assertFalse(subscription.isClosed());
        }
    }

    @Test
    void holder_shouldRemoveSubscriptionOnClose() {
        try (var subscription = subscriptions.createForUser(USER_ID)) {}
        verify(subscriptions).removeSubscription(argThat(sub -> sub.getUserId().equals(USER_ID)));
    }

    @Test
    void subscription_shouldReturnDataWhenNotified_withCorrectUserId() throws ExecutionException, InterruptedException {
        try (var subscription = subscriptions.createForUser(USER_ID)) {
            CompletableFuture<String> future = waitingFuture(subscription);

            subscription.notify(DATA_1);
            assertEquals(DATA_1, future.get());
        }
    }

    @Test
    void subscription_shouldReturnData_forCorrectUserId() throws ExecutionException, InterruptedException {
        try (var subscription = subscriptions.createForUser(USER_ID)) {
            CompletableFuture<String> future = waitingFuture(subscription);

            subscriptions.notifyByUser(USER_ID_2, DATA_2);
            subscriptions.notifyByUser(USER_ID, DATA_1);

            assertEquals(DATA_1, future.get());
        }
    }

    @Test
    void subscription_shouldReturnNull_ifClosedWhileWaiting() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future;

        try (var subscription = subscriptions.createForUser(USER_ID)) {
            future = waitingFuture(subscription);
        }

        assertNull(future.get());
    }

    @Test
    void subscription_shouldWork_inSingleThread() throws InterruptedException {
        try (var subscription = subscriptions.createForUser(USER_ID)) {
            subscription.notify(DATA_1);
            assertEquals(DATA_1, subscription.waitForNext());
        }
    }

    @Test
    void subscription_shouldReturnLastNotification() throws InterruptedException {
        try (var subscription = subscriptions.createForUser(USER_ID)) {
            subscription.notify(DATA_1);
            subscription.notify(DATA_2);

            assertEquals(DATA_2, subscription.waitForNext());
        }
    }

    @Test
    void subscriptions_shouldReturnData_whenMultipleWithSameUserId() throws InterruptedException {
        try (var sub1 = subscriptions.createForUser(USER_ID);
             var sub2 = subscriptions.createForUser(USER_ID))
        {
            subscriptions.notifyByUser(USER_ID, DATA_1);
            assertEquals(DATA_1, sub1.waitForNext());
            assertEquals(DATA_1, sub2.waitForNext());
        }
    }

    @Test
    void subscription_shouldNotThrow_ifClosedMultipleTimes() {
        assertDoesNotThrow(() -> {
            var subscription = subscriptions.createForUser(USER_ID);
            subscription.close();
            subscription.close();
        });
    }

    private static CompletableFuture<String> waitingFuture(StreamingSubscription<String> subscription) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return subscription.waitForNext();
            } catch (InterruptedException e) {
                return "interrupted";
            }
        });
    }
}
