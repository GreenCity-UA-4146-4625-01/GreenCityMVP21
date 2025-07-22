package greencity.mapping;

import greencity.dto.eventcomment.EventShortInfoUserVO;
import greencity.dto.user.UserVO;
import org.modelmapper.AbstractConverter;

public class UserToEventShortInfoUserVOMapper extends AbstractConverter<UserVO, EventShortInfoUserVO> {
    @Override
    protected EventShortInfoUserVO convert(UserVO user) {
        return EventShortInfoUserVO.builder()
                .id(user.getId())
                .name(user.getName())
                .userProfilePicturePath(user.getProfilePicturePath())
                .build();
    }
}

