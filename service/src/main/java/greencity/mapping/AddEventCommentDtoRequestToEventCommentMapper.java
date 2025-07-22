package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;

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
public class AddEventCommentDtoRequestToEventCommentMapper extends AbstractConverter<AddEventCommentDtoRequest, EventComment> {
    @Override
    protected EventComment convert(AddEventCommentDtoRequest source) {
        return EventComment.builder().text(source.getText()).build();
    }
}

