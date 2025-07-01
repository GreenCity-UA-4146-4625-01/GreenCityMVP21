package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import greencity.enums.TagType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoMapperTest {
    @InjectMocks
    private EcoNewsDtoMapper mapper;

    @Test
    void convertNoTags() {
        EcoNews news = ModelUtils.getEcoNews();
        news.setTags(List.of());

        EcoNewsDto dto = mapper.convert(news);
        assertEquals(dto.getId(), news.getId());
        assertEquals(dto.getTitle(), news.getTitle());
        assertEquals(dto.getContent(), news.getText());
        assertEquals(dto.getAuthor().getId(), news.getAuthor().getId());
        assertEquals(dto.getAuthor().getName(), news.getAuthor().getName());
        assertEquals(dto.getCreationDate(), news.getCreationDate());
        assertEquals(dto.getImagePath(), news.getImagePath());
        assertEquals(dto.getLikes(), news.getUsersLikedNews().size());
        assertEquals(dto.getDislikes(), news.getUsersDislikedNews().size());
        assertEquals(dto.getShortInfo(), news.getShortInfo());
        assertEquals(0, dto.getTags().size());
        assertEquals(0, dto.getTagsUa().size());
    }

    @Test
    void convertWithTagsEn() {
        EcoNews news = ModelUtils.getEcoNews();
        news.setTags(List.of(
                Tag.builder()
                        .id(1L)
                        .type(TagType.ECO_NEWS)
                        .tagTranslations(List.of(
                                TagTranslation.builder()
                                        .language(ModelUtils.getLanguage())
                                        .name("Test Tag")
                                        .build()
                        ))
                        .build()
        ));

        EcoNewsDto dto = mapper.convert(news);
        assertEquals(1, dto.getTags().size());
        assertEquals(0, dto.getTagsUa().size());
    }

    @Test
    void convertWithTagsUa() {
        EcoNews news = ModelUtils.getEcoNews();
        news.setTags(List.of(
                Tag.builder()
                        .id(1L)
                        .type(TagType.ECO_NEWS)
                        .tagTranslations(List.of(
                                TagTranslation.builder()
                                        .language(ModelUtils.getLanguageUa())
                                        .name("Test Tag")
                                        .build()
                        ))
                        .build()
        ));

        EcoNewsDto dto = mapper.convert(news);
        assertEquals(0, dto.getTags().size());
        assertEquals(1, dto.getTagsUa().size());
    }

    @Test
    void convertWithComments() {
        EcoNews news = ModelUtils.getEcoNews();
        news.setEcoNewsComments(List.of(
                EcoNewsComment.builder()
                        .deleted(false)
                        .text("not deleted")
                        .build()
        ));

        EcoNewsDto dto = mapper.convert(news);
        assertEquals(1, dto.getCountComments());
    }

    @Test
    void convertWithDeletedComments() {
        EcoNews news = ModelUtils.getEcoNews();
        news.setEcoNewsComments(List.of(
                EcoNewsComment.builder()
                        .deleted(true)
                        .text("deleted")
                        .build()
        ));

        EcoNewsDto dto = mapper.convert(news);
        assertEquals(0, dto.getCountComments());
    }
}
