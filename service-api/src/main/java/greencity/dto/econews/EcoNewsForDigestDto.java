package greencity.dto.econews;

import lombok.Builder;

@Builder
public record EcoNewsForDigestDto(
        String title,
        String truncatedText,
        String url,
        String imagePath
) {}
