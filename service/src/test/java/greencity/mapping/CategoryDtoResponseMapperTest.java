package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CategoryDtoResponseMapperTest {
    @InjectMocks
    CategoryDtoResponseMapper categoryDtoResponseMapper;

    @Test
    void convertSuccess() {
        Category category = new Category();
        category.setId(1L);
        category.setName("TestName");

        CategoryDtoResponse expectedCategoryDtoResponse = CategoryDtoResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .build();

        assertEquals(expectedCategoryDtoResponse, (categoryDtoResponseMapper.convert(category)));
        assertEquals("TestName", categoryDtoResponseMapper.convert(category).getName());
        assertEquals(1L, categoryDtoResponseMapper.convert(category).getId());
    }

    @Test
    void convertWithNullSuccess() {
        Category category = new Category();

        CategoryDtoResponse expectedCategoryDtoResponse = new CategoryDtoResponse();

        assertEquals(expectedCategoryDtoResponse, (categoryDtoResponseMapper.convert(category)));
        assertNull(categoryDtoResponseMapper.convert(category).getName());
        assertNull(categoryDtoResponseMapper.convert(category).getId());
    }
}
