package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.entity.EcoNewsComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EcoNewsCommentVOMapperTest {
    @InjectMocks
    EcoNewsCommentVOMapper mapper;
    
    @Test
    void convertWithNullParent() {
        EcoNewsComment comment = ModelUtils.getEcoNewsComment();
        comment.setUsersLiked(Set.of(ModelUtils.getUser()));

        EcoNewsCommentVO vo = mapper.convert(comment);
        
        assertEquals(comment.getId(), vo.getId());
        assertEquals(ModelUtils.getUserVO(), vo.getUser());
        assertEquals(comment.getCreatedDate(), vo.getCreatedDate());
        assertEquals(comment.getModifiedDate(), vo.getModifiedDate());
        assertEquals(comment.getText(), vo.getText());
        assertEquals(comment.isDeleted(), vo.isDeleted());
        assertEquals(comment.isCurrentUserLiked(), vo.isCurrentUserLiked());
        assertEquals(Set.of(ModelUtils.getUserVO()), vo.getUsersLiked());
        assertEquals(ModelUtils.getEcoNewsVO(), vo.getEcoNews());
    }

    @Test
    void convertWithParent() {
        EcoNewsComment comment = ModelUtils.getEcoNewsComment();
        comment.setUsersLiked(Set.of());

        EcoNewsComment parent = new EcoNewsComment(comment.getId() + 1, comment.getText(), comment.getCreatedDate(),
                comment.getModifiedDate(), null, List.of(comment), comment.getUser(),
                comment.getEcoNews(), comment.isDeleted(), comment.isCurrentUserLiked(), comment.getUsersLiked());
        comment.setParentComment(parent);

        EcoNewsCommentVO vo = mapper.convert(comment);
        assertEquals(parent.getId(), vo.getParentComment().getId());
    }
}
