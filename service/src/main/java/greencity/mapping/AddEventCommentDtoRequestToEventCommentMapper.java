package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;

public class AddEventCommentDtoRequestToEventCommentMapper extends AbstractConverter<AddEventCommentDtoRequest, EventComment> {
    @Override
    protected EventComment convert(AddEventCommentDtoRequest source) {
        return EventComment.builder().text(source.getText()).build();
    }
}

