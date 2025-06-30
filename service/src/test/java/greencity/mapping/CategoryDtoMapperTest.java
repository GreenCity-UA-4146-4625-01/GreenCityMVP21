package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import greencity.entity.Category;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CategoryDtoMapperTest {
    @InjectMocks
    CategoryDtoMapper categoryDtoMapper;

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void convertSuccess() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("TestName");

        Category expectedCategory = Category.builder()
                .name("TestName")
                .build();

        assertEquals(expectedCategory, categoryDtoMapper.convert(categoryDto));
        assertEquals("TestName", categoryDtoMapper.convert(categoryDto).getName());
    }

    @Test
    void convertFailureWithWrongName() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("T");

        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(categoryDto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void convertFailureWithoutFields() {
        CategoryDto categoryDto = new CategoryDto();

        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(categoryDto);

        assertFalse(violations.isEmpty());
    }
}
