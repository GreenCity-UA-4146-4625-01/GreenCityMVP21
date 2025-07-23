package greencity.mapping;

import greencity.dto.eventcomment.EventShortInfoUserVO;
import greencity.dto.user.UserVO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserToEventShortInfoUserVOMapperTest {

    private final UserToEventShortInfoUserVOMapper mapper = new UserToEventShortInfoUserVOMapper();

    @Test
    void convertTest() {
        UserVO userVO = UserVO.builder()
                .id(7L)
                .name("Short Info User")
                .profilePicturePath("avatar/path.png")
                .build();

        EventShortInfoUserVO result = mapper.convert(userVO);

        assertEquals(userVO.getId(), result.getId());
        assertEquals(userVO.getName(), result.getName());
        assertEquals(userVO.getProfilePicturePath(), result.getUserProfilePicturePath());
    }
}
