package greencity.config;

import greencity.service.EmailSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulingConfig {

    @Bean
    public ApplicationRunner scheduleEmailSending(
            TaskScheduler scheduler,
            @Value("${subscriptions.time-between-check-pending}") Duration timeBetween,
            EmailSubscriptionService emailSubscriptionService) {
        return args -> {
            log.info("Will send emails every {}", DurationStyle.SIMPLE.print(timeBetween));
            scheduler.scheduleAtFixedRate(emailSubscriptionService::sendEmailsIfNeeded, timeBetween);
        };
    }
}
