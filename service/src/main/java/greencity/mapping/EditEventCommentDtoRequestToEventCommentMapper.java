package greencity.mapping;

import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.modelmapper.AbstractConverter;

public class EditEventCommentDtoRequestToEventCommentMapper extends AbstractConverter<EditEventCommentDtoRequest, EventComment> {
    @Override
    protected EventComment convert(EditEventCommentDtoRequest dto) {
        return EventComment.builder().text(dto.getText()).build();
    }
}

