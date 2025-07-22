package greencity.mapping;

import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventcomment.EventCommentDtoResponse;
import greencity.dto.eventcomment.EventShortInfoUserVO;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;

import java.util.HashSet;
import java.util.Set;

public class EventCommentToDtoResponseMapper extends AbstractConverter<EventComment, EventCommentDtoResponse> {
    @Override
    protected EventCommentDtoResponse convert(EventComment source) {
        EventCommentDtoResponse response = new EventCommentDtoResponse();

        response.setId(source.getId());

        EventCommentAuthorDto authorDto = new EventCommentAuthorDto();
        User user = source.getUser();
        if (user != null) {
            authorDto.setId(user.getId());
            authorDto.setName(user.getName());
            authorDto.setUserProfilePicturePath(user.getProfilePicturePath());
        }
        response.setAuthor(authorDto);

        response.setText(source.getText());
        response.setModifiedDate(source.getModifiedDate());

        response.setReplies(source.getReplies() != null ? source.getReplies().size() : 0);

        response.setLikes(source.getUsersLiked() != null ? source.getUsersLiked().size() : 0);

        Set<EventShortInfoUserVO> mentionedUsers = new HashSet<>();
        if (source.getMentionedUsers() != null) {
            for (User mentionedUser : source.getMentionedUsers()) {
                EventShortInfoUserVO vo = new EventShortInfoUserVO();
                vo.setId(mentionedUser.getId());
                vo.setName(mentionedUser.getName());
                vo.setUserProfilePicturePath(mentionedUser.getProfilePicturePath());
                mentionedUsers.add(vo);
            }
        }
        response.setMentionedUser(mentionedUsers);

        return response;
    }
}

