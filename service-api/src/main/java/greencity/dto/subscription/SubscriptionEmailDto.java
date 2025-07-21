package greencity.dto.subscription;

import greencity.dto.econews.EcoNewsForDigestDto;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record SubscriptionEmailDto(
        String email,
        UUID subscriptionId,
        List<EcoNewsForDigestDto> news
) {}
