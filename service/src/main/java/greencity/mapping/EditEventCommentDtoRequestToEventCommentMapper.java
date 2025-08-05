package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Mapper class converting {@link EditEventCommentDtoRequest} to {@link EventComment}.
 * <p>
 * Used to update the comment text based on the received DTO during editing.
 * Only the text field is transferred to the new EventComment object.
 * </p>
 *
 * Example usage with ModelMapper:
 * <pre>{@code
 * modelMapper.addConverter(new EditEventCommentDtoRequestToEventCommentMapper());
 * }</pre>
 */
@Component
public class EditEventCommentDtoRequestToEventCommentMapper extends AbstractConverter<EditEventCommentDtoRequest, EventComment> {

    public void update(EventComment comment, EditEventCommentDtoRequest source, Set<User> mentionedUsers) {
        comment.setText(source.getText());
        comment.setMentionedUsers(mentionedUsers);
        comment.setModifiedDate(LocalDateTime.now());
    }


    @Override
    protected EventComment convert(EditEventCommentDtoRequest dto) {
        return EventComment.builder()
                .text(dto.getText())
                .build();
    }
}

