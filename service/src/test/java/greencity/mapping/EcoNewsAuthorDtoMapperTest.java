package greencity.mapping;

import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.User;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EcoNewsAuthorDtoMapperTest {
    @InjectMocks
    EcoNewsAuthorDtoMapper mapper;

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void convertSuccess() {
        User author = User.builder()
                .id(1L)
                .name("test")
                .build();

        EcoNewsAuthorDto expected = new EcoNewsAuthorDto(1L, "test");
        EcoNewsAuthorDto actual = mapper.convert(author);

        assertEquals(expected, actual);
        assertTrue(validator.validate(actual).isEmpty());
    }
}
