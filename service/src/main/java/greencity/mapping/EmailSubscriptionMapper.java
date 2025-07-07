package greencity.mapping;

import greencity.dto.subscription.SubscriptionDto;
import greencity.entity.EmailSubscription;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EmailSubscriptionMapper extends AbstractConverter<EmailSubscription, SubscriptionDto> {
    @Override
    protected SubscriptionDto convert(EmailSubscription emailSubscription) {
        return new SubscriptionDto(emailSubscription.getId());
    }
}
