package greencity.mapping;

import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.user.UserVO;
import org.modelmapper.AbstractConverter;

/**
 * Mapper class that converts {@link UserVO} to {@link EventCommentAuthorDto}.
 * <p>
 * Used to transform user data into a simplified DTO
 * for displaying event comment author information.
 * </p>
 *
 * Example of mapped fields:
 * <ul>
 *     <li>id</li>
 *     <li>name</li>
 *     <li>userProfilePicturePath</li>
 * </ul>
 *
 * Usage with ModelMapper:
 * <pre>{@code
 * modelMapper.addConverter(new UserToEventCommentAuthorDtoMapper());
 * }</pre>
 */

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

