package greencity.mapping;

import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.entity.EventComment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddEventCommentDtoRequestToEventCommentMapperTest {

    private final AddEventCommentDtoRequestToEventCommentMapper mapper = new AddEventCommentDtoRequestToEventCommentMapper();

    @Test
    void convertTest() {
        AddEventCommentDtoRequest dto = new AddEventCommentDtoRequest();
        dto.setText("Test comment");
        dto.setParentCommentId(123L);
        dto.getMentionedUserIds().add(1L);

        EventComment result = mapper.convert(dto);

        assertEquals(dto.getText(), result.getText());
    }
}
