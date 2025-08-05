package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.EventComment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Mapper class converting {@link AddEventCommentDtoRequest} to {@link EventComment} entity.
 * <p>
 * Used to create a new EventComment entity from the data received in the add comment request DTO.
 * Currently, only the text field is mapped.
 * </p>
 *
 * Example usage with ModelMapper:
 * <pre>{@code
 * modelMapper.addConverter(new AddEventCommentDtoRequestToEventCommentMapper());
 * }</pre>
 */
@Component
public class AddEventCommentDtoRequestToEventCommentMapper extends AbstractConverter<AddEventCommentDtoRequest, EventComment> {

    public EventComment convert(AddEventCommentDtoRequest source, Set<User> mentionedUsers) {
        return EventComment.builder()
                .text(source.getText())
                .mentionedUsers(mentionedUsers)
                .build();
    }

    @Override
    protected EventComment convert(AddEventCommentDtoRequest source) {
        return EventComment.builder()
                .text(source.getText())
                .build();
    }
}

