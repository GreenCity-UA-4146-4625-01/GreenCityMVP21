package greencity.sse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class StreamingSubscription<T> implements AutoCloseable {

    private final AtomicReference<T> currentValue = new AtomicReference<>();

    private final SubscriptionHolder<T> holder;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    @Getter
    private final Long userId;

    public synchronized T waitForNext() throws InterruptedException {
        while (currentValue.get() == null && !isClosed()) wait();
        return currentValue.getAndSet(null);
    }

    public synchronized void notify(T data) {
        currentValue.set(data);
        notifyAll();
    }

    @Override
    public void close() {
        holder.removeSubscription(this);
        isClosed.set(true);

        synchronized (this) {
            notifyAll();
        }
    }

    public boolean isClosed() {
        return isClosed.get();
    }
}
