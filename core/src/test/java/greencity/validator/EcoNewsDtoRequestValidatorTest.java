package greencity.validator;

import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.exception.exceptions.InvalidURLException;
import greencity.exception.exceptions.WrongCountOfTagsException;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EcoNewsDtoRequestValidatorTest {
    @InjectMocks
    private EcoNewsDtoRequestValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void isValid_validTagsAndValidSource_returnsTrue() {
        AddEcoNewsDtoRequest dto = new AddEcoNewsDtoRequest();
        dto.setTags(Arrays.asList("tag1", "tag2"));
        dto.setSource("https://example.com");
        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_validTagsAndNullSource_returnsTrue() {
        AddEcoNewsDtoRequest dto = new AddEcoNewsDtoRequest();
        dto.setTags(Arrays.asList("tag1", "tag2"));
        dto.setSource(null);
        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_validTagsAndEmptySource_returnsTrue() {
        AddEcoNewsDtoRequest dto = new AddEcoNewsDtoRequest();
        dto.setTags(Arrays.asList("tag1", "tag2"));
        dto.setSource("");
        assertThat(validator.isValid(dto, context)).isTrue();
    }

    @Test
    void isValid_emptyTags_throwsWrongCountExecption() {
        AddEcoNewsDtoRequest dto = new AddEcoNewsDtoRequest();
        dto.setTags(Collections.emptyList());
        dto.setSource(null);

        assertThatThrownBy(() -> validator.isValid(dto, context))
            .isInstanceOf(WrongCountOfTagsException.class)
            .hasMessageContaining(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION);
    }

    @Test
    void isValid_tooManyTags_throwsWrongCountExecption() {
        AddEcoNewsDtoRequest dto = new AddEcoNewsDtoRequest();
        dto.setTags(Arrays.asList("tag1", "tag2", "tag3", "tag4"));
        dto.setSource(null);

        assertThatThrownBy(() -> validator.isValid(dto, context))
            .isInstanceOf(WrongCountOfTagsException.class)
            .hasMessageContaining(ErrorMessage.WRONG_COUNT_OF_TAGS_EXCEPTION);
    }

    @Test
    void isValid_invalidSource_throwsConstaintExecption() {
        AddEcoNewsDtoRequest dto = new AddEcoNewsDtoRequest();
        dto.setTags(Arrays.asList("tag1", "tag2"));
        dto.setSource("not-a-valid-url");

        assertThatThrownBy(() -> validator.isValid(dto, context))
            .isInstanceOf(InvalidURLException.class)
            .hasMessageContaining(ErrorMessage.MALFORMED_URL);
    }
}
