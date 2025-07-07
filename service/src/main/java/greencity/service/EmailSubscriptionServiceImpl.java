package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.subscription.SubscriptionDto;
import greencity.entity.EmailSubscription;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.SubscriptionAlreadyExistsException;
import greencity.repository.EmailSubscriptionRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailSubscriptionServiceImpl implements EmailSubscriptionService {

    private final EmailSubscriptionRepo emailSubscriptionRepo;
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
        EmailSubscription subscription = emailSubscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_SUBSCRIPTION));

        emailSubscriptionRepo.delete(subscription);
    }

    @Override
    public boolean shouldSendEmail(UUID subscriptionId) {
        throw new IllegalStateException("Not implemented yet");
    }
}
