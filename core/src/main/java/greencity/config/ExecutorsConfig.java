package greencity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration for thread pools & ExecutorServices.
 */
@Configuration
public class ExecutorsConfig {

    /** @return The executor for notification tasks. */
    @Bean
    public ExecutorService streamingNotificationsExecutor() {
        return Executors.newCachedThreadPool();
    }
}
