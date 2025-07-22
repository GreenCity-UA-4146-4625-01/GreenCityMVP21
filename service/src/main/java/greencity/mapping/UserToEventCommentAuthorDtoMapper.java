package greencity.mapping;

import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.user.UserVO;
import org.modelmapper.AbstractConverter;

public class UserToEventCommentAuthorDtoMapper extends AbstractConverter<UserVO, EventCommentAuthorDto> {
    @Override
    protected EventCommentAuthorDto convert(UserVO user) {

        return EventCommentAuthorDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userProfilePicturePath(user.getProfilePicturePath())
                .build();
    }
}

