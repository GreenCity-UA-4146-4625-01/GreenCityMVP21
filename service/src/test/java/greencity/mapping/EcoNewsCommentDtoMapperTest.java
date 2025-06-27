package greencity.mapping;

import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.CommentStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EcoNewsCommentDtoMapperTest {
    @InjectMocks
    EcoNewsCommentDtoMapper mapper;

    private static User authorUser;

    @BeforeAll
    static void setup() {
        authorUser = User.builder()
                .id(1L)
                .name("User")
                .profilePicturePath("http://localhost/image.png")
                .build();
    }

    @Test
    void convertSuccessDeletedComment() {
        EcoNewsComment comment = EcoNewsComment.builder()
                .id(1L)
                .createdDate(LocalDateTime.now().minusDays(1))
                .modifiedDate(LocalDateTime.now().minusDays(1))
                .deleted(true)
                .build();

        EcoNewsCommentDto dto = mapper.convert(comment);
        assertEquals(CommentStatus.DELETED, dto.getStatus());
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getModifiedDate(), dto.getModifiedDate());
    }

    @Test
    void convertSuccessOriginalComment() {
        LocalDateTime createdDate = LocalDateTime.now().minusDays(1);

        EcoNewsComment comment = EcoNewsComment.builder()
                .id(1L)
                .createdDate(createdDate)
                .modifiedDate(createdDate)
                .text("text")
                .user(authorUser)
                .usersLiked(Set.of(authorUser))
                .currentUserLiked(true)
                .build();

        EcoNewsCommentDto dto = mapper.convert(comment);
        assertEquals(CommentStatus.ORIGINAL, dto.getStatus());
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getModifiedDate(), dto.getModifiedDate());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.isCurrentUserLiked(), dto.isCurrentUserLiked());
        assertEquals(1, dto.getLikes());
    }

    @Test
    void convertSuccessEditedComment() {
        EcoNewsComment comment = EcoNewsComment.builder()
                .id(1L)
                .createdDate(LocalDateTime.now().minusDays(1))
                .modifiedDate(LocalDateTime.now())
                .text("text")
                .user(authorUser)
                .usersLiked(Set.of(authorUser))
                .currentUserLiked(true)
                .build();

        EcoNewsCommentDto dto = mapper.convert(comment);
        assertEquals(CommentStatus.EDITED, dto.getStatus());
    }
}
