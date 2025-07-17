package greencity.mapping;

import greencity.dto.econews.EcoNewsForDigestDto;
import greencity.entity.EcoNews;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EcoNewsForDigestDtoMapper extends AbstractConverter<EcoNews, EcoNewsForDigestDto> {
    @Override
    protected EcoNewsForDigestDto convert(EcoNews source) {
        String fullText = source.getText();
        String truncatedText = fullText.length() > 100 ? fullText.substring(0, 100) + "..." : fullText;

        return EcoNewsForDigestDto.builder()
                .url(source.getSource())
                .truncatedText(truncatedText)
                .title(source.getTitle())
                .imagePath(source.getImagePath())
                .build();
    }
}
