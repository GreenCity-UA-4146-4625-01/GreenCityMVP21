package greencity.mapping;

import greencity.dto.econews.EcoNewsForDigestDto;
import greencity.entity.EcoNews;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EcoNewsForDigestDtoMapperTest {

    @InjectMocks
    private EcoNewsForDigestDtoMapper mapper;

    @Test
    void mapper_preservesShortText() {
        EcoNews news = EcoNews.builder()
                .text("short text")
                .build();

        EcoNewsForDigestDto dto = mapper.convert(news);
        assertEquals(news.getText(), dto.truncatedText());
    }

    @Test
    void mapper_truncatesLongText() {
        EcoNews news = EcoNews.builder()
                .text("long text".repeat(1000))
                .build();

        EcoNewsForDigestDto dto = mapper.convert(news);
        assertNotEquals(news.getText(), dto.truncatedText());
        assertTrue(dto.truncatedText().endsWith("..."));
        assertTrue(dto.truncatedText().length() < news.getText().length());
    }

    @Test
    void mapper_handlesNullCorrectly() {
        assertNull(mapper.convert((EcoNews) null));
    }

    @Test
    void mapper_mapsFieldsCorrectly() {
        EcoNews news = EcoNews.builder()
                .text("short text")
                .source("https://google.com/")
                .title("Title")
                .imagePath("https://google.com/favicon.ico")
                .build();

        EcoNewsForDigestDto dto = mapper.convert(news);
        assertEquals(news.getText(), dto.truncatedText());
        assertEquals(news.getTitle(), dto.title());
        assertEquals(news.getImagePath(), dto.imagePath());
        assertEquals(news.getSource(), dto.url());
    }
}
