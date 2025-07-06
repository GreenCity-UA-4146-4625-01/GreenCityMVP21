package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemRequestDtoMapperTest {

    @InjectMocks
    private ShoppingListItemRequestDtoMapper mapper;

    private ShoppingListItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = ShoppingListItemRequestDto.builder()
                .id(1L)
                .build();
    }

    @Test
    void convert_WithValidDto_ShouldReturnUserShoppingListItem() {
        Long expectedId = 1L;
        requestDto.setId(expectedId);

        UserShoppingListItem result = mapper.convert(requestDto);

        assertNotNull(result);
        assertNotNull(result.getShoppingListItem());
        assertEquals(expectedId, result.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, result.getStatus());
        assertNull(result.getId());
        assertNull(result.getHabitAssign());
        assertNull(result.getDateCompleted());
    }

    @Test
    void convert_WithDifferentId_ShouldSetCorrectId() {
        Long expectedId = 999L;
        requestDto.setId(expectedId);

        UserShoppingListItem result = mapper.convert(requestDto);

        assertNotNull(result);
        assertNotNull(result.getShoppingListItem());
        assertEquals(expectedId, result.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, result.getStatus());
    }

    @Test
    void convert_WithMinimumValidId_ShouldReturnUserShoppingListItem() {
        Long expectedId = 1L;
        requestDto.setId(expectedId);

        UserShoppingListItem result = mapper.convert(requestDto);

        assertNotNull(result);
        assertNotNull(result.getShoppingListItem());
        assertEquals(expectedId, result.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, result.getStatus());
    }

    @Test
    void convert_WithLargeId_ShouldReturnUserShoppingListItem() {
        Long expectedId = Long.MAX_VALUE;
        requestDto.setId(expectedId);

        UserShoppingListItem result = mapper.convert(requestDto);

        assertNotNull(result);
        assertNotNull(result.getShoppingListItem());
        assertEquals(expectedId, result.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, result.getStatus());
    }

    @Test
    void convert_ShouldAlwaysSetStatusToActive() {
        requestDto.setId(123L);

        UserShoppingListItem result = mapper.convert(requestDto);

        assertNotNull(result);
        assertEquals(ShoppingListItemStatus.ACTIVE, result.getStatus());
    }

    @Test
    void convert_ShouldCreateNewShoppingListItemInstance() {
        requestDto.setId(456L);

        UserShoppingListItem result1 = mapper.convert(requestDto);
        UserShoppingListItem result2 = mapper.convert(requestDto);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
        assertNotSame(result1.getShoppingListItem(), result2.getShoppingListItem());
        assertEquals(result1.getShoppingListItem().getId(), result2.getShoppingListItem().getId());
    }

    @Test
    void convert_ShouldOnlySetIdInShoppingListItem() {
        requestDto.setId(789L);

        UserShoppingListItem result = mapper.convert(requestDto);
        ShoppingListItem shoppingListItem = result.getShoppingListItem();

        assertNotNull(result);
        assertNotNull(result.getShoppingListItem());
        assertEquals(789L, result.getShoppingListItem().getId());
        assertNull(shoppingListItem.getUserShoppingListItems());
        assertNull(shoppingListItem.getHabits());
        assertNull(shoppingListItem.getTranslations());
    }

    @Test
    void convert_ShouldCreateBuilderBasedObjects() {
        requestDto.setId(100L);

        UserShoppingListItem result = mapper.convert(requestDto);

        assertNotNull(result);
        assertNotNull(result.getShoppingListItem());
        assertNotNull(result.getShoppingListItem().getId());
        assertNotNull(result.getStatus());
    }

    @Test
    void convert_MultipleCalls_ShouldReturnConsistentResults() {
        Long testId = 555L;
        requestDto.setId(testId);

        UserShoppingListItem result1 = mapper.convert(requestDto);
        UserShoppingListItem result2 = mapper.convert(requestDto);
        UserShoppingListItem result3 = mapper.convert(requestDto);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertEquals(testId, result1.getShoppingListItem().getId());
        assertEquals(testId, result2.getShoppingListItem().getId());
        assertEquals(testId, result3.getShoppingListItem().getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, result1.getStatus());
        assertEquals(ShoppingListItemStatus.ACTIVE, result2.getStatus());
        assertEquals(ShoppingListItemStatus.ACTIVE, result3.getStatus());
    }
}