package greencity.mapping;

import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;

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
public class EditEventCommentDtoRequestToEventCommentMapper extends AbstractConverter<EditEventCommentDtoRequest, EventComment> {
    @Override
    protected EventComment convert(EditEventCommentDtoRequest dto) {
        return EventComment.builder().text(dto.getText()).build();
    }
}

