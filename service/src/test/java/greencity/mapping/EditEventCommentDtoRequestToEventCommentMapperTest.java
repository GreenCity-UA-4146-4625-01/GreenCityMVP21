package greencity.mapping;

import greencity.dto.eventcomment.EditEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EditEventCommentDtoRequestToEventCommentMapperTest {

    private final EditEventCommentDtoRequestToEventCommentMapper mapper = new EditEventCommentDtoRequestToEventCommentMapper();

    @Test
    void convertTest() {
        EditEventCommentDtoRequest dto = new EditEventCommentDtoRequest();
        dto.setText("Updated comment text");

        EventComment result = mapper.convert(dto);

        assertEquals(dto.getText(), result.getText());
    }
}
