package greencity.mapping;

import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.user.UserVO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserToEventCommentAuthorDtoMapperTest {

    private final UserToEventCommentAuthorDtoMapper mapper = new UserToEventCommentAuthorDtoMapper();

    @Test
    void convertTest() {
        UserVO userVO = UserVO.builder()
                .id(5L)
                .name("Test User")
                .profilePicturePath("test/path.jpg")
                .build();

        EventCommentAuthorDto result = mapper.convert(userVO);

        assertEquals(userVO.getId(), result.getId());
        assertEquals(userVO.getName(), result.getName());
        assertEquals(userVO.getProfilePicturePath(), result.getUserProfilePicturePath());
    }
}
