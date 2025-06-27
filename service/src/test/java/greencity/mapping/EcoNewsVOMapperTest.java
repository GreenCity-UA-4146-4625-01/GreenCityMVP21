package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EcoNewsVOMapperTest {

    @InjectMocks
    private EcoNewsVOMapper mapper;

    private static EcoNews createEcoNews() {
        EcoNews news = ModelUtils.getEcoNews();
        news.setTags(List.of());
        news.setUsersDislikedNews(Set.of());
        news.setUsersLikedNews(Set.of());
        news.setEcoNewsComments(List.of());
        return news;
    }

    @Test
    void convertSuccessBasic() {
        EcoNews news = createEcoNews();

        EcoNewsVO vo = mapper.convert(news);

        assertEquals(news.getId(), vo.getId());
        assertEquals(news.getAuthor().getId(), vo.getAuthor().getId());
        assertEquals(news.getAuthor().getName(), vo.getAuthor().getName());
        assertEquals(news.getAuthor().getUserStatus(), vo.getAuthor().getUserStatus());
        assertEquals(news.getAuthor().getRole(), vo.getAuthor().getRole());
        assertEquals(news.getCreationDate(), vo.getCreationDate());
        assertEquals(news.getImagePath(), vo.getImagePath());
        assertEquals(news.getSource(), vo.getSource());
        assertEquals(news.getTitle(), vo.getTitle());
        assertEquals(news.getText(), vo.getText());
    }

    @Test
    void convertSuccessWithTags() {
        EcoNews news = createEcoNews();
        news.setTags(List.of(
                Tag.builder()
                        .id(1L)
                        .tagTranslations(List.of(
                                TagTranslation.builder()
                                        .name("name")
                                        .id(1L)
                                        .language(ModelUtils.getLanguage())
                                        .build()
                        ))
                        .build()
        ));

        EcoNewsVO vo = mapper.convert(news);
        assertEquals(1, vo.getTags().size());

        TagVO tagVo = vo.getTags().getFirst();
        assertEquals(1, tagVo.getId());
        assertEquals(1, tagVo.getTagTranslations().size());

        TagTranslationVO translationVo = tagVo.getTagTranslations().getFirst();
        assertEquals(1, translationVo.getId());
        assertEquals("name", translationVo.getName());
        assertEquals(ModelUtils.getLanguageVO(), translationVo.getLanguageVO());
    }

    @Test
    void convertSuccessWithUsersLiked() {
        EcoNews news = createEcoNews();
        news.setUsersLikedNews(Set.of(ModelUtils.getUser()));

        EcoNewsVO vo = mapper.convert(news);
        assertEquals(1, vo.getUsersLikedNews().size());
        assertEquals(1L, vo.getUsersLikedNews().toArray(new UserVO[0])[0].getId());
    }

    @Test
    void convertSuccessWithUsersDisliked() {
        EcoNews news = createEcoNews();
        news.setUsersDislikedNews(Set.of(ModelUtils.getUser()));

        EcoNewsVO vo = mapper.convert(news);
        assertEquals(1, vo.getUsersDislikedNews().size());
        assertEquals(1L, vo.getUsersDislikedNews().toArray(new UserVO[0])[0].getId());
    }

    @Test
    void convertSuccessWithComments() {
        EcoNews news = createEcoNews();
        news.setEcoNewsComments(List.of(
                ModelUtils.getEcoNewsComment()
        ));

        EcoNewsVO vo = mapper.convert(news);
        assertEquals(1, vo.getEcoNewsComments().size());

        EcoNewsCommentVO commentVo = vo.getEcoNewsComments().getFirst();
        EcoNewsComment comment = news.getEcoNewsComments().getFirst();

        assertEquals(comment.getId(), commentVo.getId());
        assertEquals(comment.getCreatedDate(), commentVo.getCreatedDate());
        assertEquals(comment.getModifiedDate(), commentVo.getModifiedDate());
        assertEquals(comment.getText(), commentVo.getText());
        assertEquals(comment.isDeleted(), commentVo.isDeleted());
        assertEquals(comment.isCurrentUserLiked(), commentVo.isCurrentUserLiked());

        assertEquals(comment.getUser().getId(), commentVo.getUser().getId());
        assertEquals(comment.getUser().getName(), commentVo.getUser().getName());
        assertEquals(comment.getUser().getUserStatus(), commentVo.getUser().getUserStatus());
        assertEquals(comment.getUser().getRole(), commentVo.getUser().getRole());
    }
}
