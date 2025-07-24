package greencity.mapping;

import greencity.dto.eventcomment.EventShortInfoUserVO;
import greencity.dto.user.UserVO;
import org.modelmapper.AbstractConverter;
/**
 * Mapper class for converting {@link UserVO} to {@link EventShortInfoUserVO}.
 * <p>
 * Provides a lightweight representation of a user
 * for use in event-related DTOs (e.g., comment lists, participants).
 * </p>
 *
 * Mapped fields:
 * <ul>
 *     <li>id</li>
 *     <li>name</li>
 *     <li>userProfilePicturePath</li>
 * </ul>
 *
 * Example usage with ModelMapper:
 * <pre>{@code
 * modelMapper.addConverter(new UserToEventShortInfoUserVOMapper());
 * }</pre>
 */
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

