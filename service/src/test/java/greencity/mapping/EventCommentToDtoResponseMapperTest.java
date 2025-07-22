package greencity.mapping;

import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.eventcomment.EventShortInfoUserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EventCommentToDtoResponseMapperTest {

    private final EventCommentToDtoResponseMapper mapper = new EventCommentToDtoResponseMapper();

    @Test
    void convertTest() {
        User author = User.builder()
                .id(1L)
                .name("John Doe")
                .profilePicturePath("path/to/profile.jpg")
                .build();

        User mentionedUser = User.builder()
                .id(2L)
                .name("Mentioned User")
                .profilePicturePath("path/to/mentioned.jpg")
                .build();

        EventComment comment = EventComment.builder()
                .id(10L)
                .user(author)
                .text("Sample Comment")
                .modifiedDate(LocalDateTime.now())
                .usersLiked(Set.of(author))
                .mentionedUsers(Set.of(mentionedUser))
                .replies(List.of(EventComment.builder().id(99L).build()))
                .build();

        EventCommentDtoResponse result = mapper.convert(comment);

        assertEquals(comment.getId(), result.getId());

        EventCommentAuthorDto resultAuthor = result.getAuthor();
        assertNotNull(resultAuthor);
        assertEquals(author.getId(), resultAuthor.getId());
        assertEquals(author.getName(), resultAuthor.getName());
        assertEquals(author.getProfilePicturePath(), resultAuthor.getUserProfilePicturePath());

        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getModifiedDate(), result.getModifiedDate());
        assertEquals(1, result.getReplies());
        assertEquals(1, result.getLikes());

        assertEquals(1, result.getMentionedUser().size());
        EventShortInfoUserVO mentioned = result.getMentionedUser().iterator().next();
        assertEquals(mentionedUser.getId(), mentioned.getId());
        assertEquals(mentionedUser.getName(), mentioned.getName());
        assertEquals(mentionedUser.getProfilePicturePath(), mentioned.getUserProfilePicturePath());
    }
}
