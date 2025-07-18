package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.econews.EcoNewsForDigestDto;
import greencity.dto.subscription.SubscriptionDto;
import greencity.dto.subscription.SubscriptionEmailDto;
import greencity.entity.EcoNews;
import greencity.entity.EmailSubscription;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.SubscriptionAlreadyExistsException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EmailSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSubscriptionServiceImpl implements EmailSubscriptionService {

    private final EmailSubscriptionRepo emailSubscriptionRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final ModelMapper modelMapper;

    @Value("${subscriptions.time-between-emails}")
    private final Duration timeBetweenEmails;

    private final RestClient restClient;

    @Override
    public SubscriptionDto createSubscription(String email) {
        if (emailSubscriptionRepo.findByEmail(email) != null) {
            throw new SubscriptionAlreadyExistsException(ErrorMessage.ALREADY_SUBSCRIBED);
        }

        EmailSubscription subscription = EmailSubscription.builder()
                .email(email)
                .build();

        emailSubscriptionRepo.save(subscription);
        return modelMapper.map(subscription, SubscriptionDto.class);
    }

    @Override
    public void deleteSubscription(UUID subscriptionId) {
        EmailSubscription subscription = findSubscription(subscriptionId);

        emailSubscriptionRepo.delete(subscription);
    }

    @Override
    public List<EcoNewsForDigestDto> getNewsForNextEmail(UUID subscriptionId) {
        return getNewsForNextEmail(findSubscription(subscriptionId));
    }

    @Override
    public boolean shouldSendNewEmail(UUID subscriptionId) {
        return shouldSendNewEmail(findSubscription(subscriptionId));
    }

    @Override
    public boolean sendEmail(UUID subscriptionId) {
        EmailSubscription subscription = findSubscription(subscriptionId);

        if (!shouldSendNewEmail(subscription)) return false;

        List<EcoNewsForDigestDto> news = getNewsForNextEmail(subscription);
        if (news.isEmpty()) return false;

        emailSubscriptionRepo.updateSubscriptionLastSentEmailAt(subscriptionId);
        restClient.sendSubscriptionDigest(SubscriptionEmailDto.builder()
                .subscriptionId(subscriptionId)
                .email(subscription.getEmail())
                .news(news)
                .build());

        return true;
    }

    @Scheduled(fixedRateString = "${subscriptions.send-emails-rate}")
    public void sendEmailsIfNeeded() {
        log.info("Sending subscription emails...");

        Instant maxLastSent = Instant.now().minus(timeBetweenEmails);

        long numSent = emailSubscriptionRepo.findSubscriptionsWithPendingEmails(maxLastSent).stream()
                .filter(subscription -> sendEmail(subscription.getId()))
                .peek(subscription -> log.trace("Sent subscription email: {}", subscription.getId()))
                .count();

        log.info("Sent {} emails to subscriptions", numSent);
    }

    private EmailSubscription findSubscription(UUID subscriptionId) {
        return emailSubscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_SUBSCRIPTION));
    }

    private boolean shouldSendNewEmail(EmailSubscription subscription) {
        ZonedDateTime minimumTimestampWhenSend = subscription.getLastSentEmailAt() == null
                ? ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault())
                : subscription.getLastSentEmailAt().plus(timeBetweenEmails);

        return ZonedDateTime.now().isAfter(minimumTimestampWhenSend);
    }

    private List<EcoNewsForDigestDto> getNewsForNextEmail(EmailSubscription subscription) {
        List<EcoNews> recommendedNews = subscription.getLastSentEmailAt() == null
                ? ecoNewsRepo.getThreeLastEcoNews()
                : ecoNewsRepo.getThreeEcoNewsCreatedAfter(subscription.getLastSentEmailAt());

        return recommendedNews.stream()
                .map(news -> modelMapper.map(news, EcoNewsForDigestDto.class))
                .collect(Collectors.toList());
    }
}
