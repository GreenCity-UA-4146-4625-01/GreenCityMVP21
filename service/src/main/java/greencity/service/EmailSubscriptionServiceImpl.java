package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.econews.EcoNewsForDigestDto;
import greencity.dto.subscription.SubscriptionDto;
import greencity.entity.EcoNews;
import greencity.entity.EmailSubscription;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.SubscriptionAlreadyExistsException;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EmailSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailSubscriptionServiceImpl implements EmailSubscriptionService {

    private final EmailSubscriptionRepo emailSubscriptionRepo;
    private final EcoNewsRepo ecoNewsRepo;
    private final ModelMapper modelMapper;

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
        EmailSubscription subscription = findSubscription(subscriptionId);

        List<EcoNews> recommendedNews = subscription.getLastSentEmailAt() == null
                ? ecoNewsRepo.getThreeLastEcoNews()
                : ecoNewsRepo.getThreeEcoNewsCreatedAfter(subscription.getLastSentEmailAt());

        return recommendedNews.stream()
                .map(news -> modelMapper.map(news, EcoNewsForDigestDto.class))
                .collect(Collectors.toList());
    }

    private EmailSubscription findSubscription(UUID subscriptionId) {
        return emailSubscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_SUBSCRIPTION));
    }
}
