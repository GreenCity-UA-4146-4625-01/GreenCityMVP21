package greencity.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequiredArgsConstructor
public class SseSubscriptionHandler<T> implements Runnable {

    private final SseEmitter emitter;
    private final StreamingSubscription<T> subscription;

    @Override
    public void run() {
        try (subscription) {
            emitter.onTimeout(subscription::close);
            emitter.onError(e -> subscription.close());

            while (!subscription.isClosed()) {
                T event = subscription.waitForNext();
                if (event == null) break;

                emitter.send(SseEmitter.event()
                        .data(event)
                        .build());
            }
        } catch (IOException | InterruptedException e) {
            emitter.completeWithError(e);
        }
    }
}
