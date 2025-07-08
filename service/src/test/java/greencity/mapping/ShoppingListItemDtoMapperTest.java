package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemDtoMapperTest {

    @InjectMocks
    private ShoppingListItemDtoMapper shoppingListItemDtoMapper;
    @Mock
    private ShoppingListItemTranslation shoppingListItemTranslation;
    @Mock
    private ShoppingListItem shoppingListItem;

    @ParameterizedTest
    @MethodSource("provideValidInputs")
    void convert_ShouldReturnCorrectDto(Long expectedId, String expectedText) {
        when(shoppingListItemTranslation.getShoppingListItem()).thenReturn(shoppingListItem);
        when(shoppingListItem.getId()).thenReturn(expectedId);
        when(shoppingListItemTranslation.getContent()).thenReturn(expectedText);

        ShoppingListItemDto result = shoppingListItemDtoMapper.convert(shoppingListItemTranslation);

        assertNotNull(result);
        assertEquals(expectedId, result.getId());
        assertEquals(expectedText, result.getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), result.getStatus());
    }

    private static Stream<Arguments> provideValidInputs() {
        return Stream.of(
                Arguments.of(1L, "Test shopping item"),
                Arguments.of(2L, null),
                Arguments.of(3L, ""),
                Arguments.of(4L, "   "),
                Arguments.of(5L, "This is a very long shopping list item text that might be used to test the mapper with longer content strings"),
                Arguments.of(6L, "Тест з українськими символами! @#$%^&*()")
        );
    }

    @Test
    void convert_ShoppingListItemTranslationWithNullShoppingListItem_ShouldThrowException() {
        when(shoppingListItemTranslation.getShoppingListItem()).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> shoppingListItemDtoMapper.convert(shoppingListItemTranslation));
    }

    @Test
    void convert_ShoppingListItemWithNullId_ShouldReturnDtoWithNullId() {
        String expectedText = "Test item";

        when(shoppingListItemTranslation.getShoppingListItem()).thenReturn(shoppingListItem);
        when(shoppingListItem.getId()).thenReturn(null);
        when(shoppingListItemTranslation.getContent()).thenReturn(expectedText);

        ShoppingListItemDto result = shoppingListItemDtoMapper.convert(shoppingListItemTranslation);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(expectedText, result.getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), result.getStatus());
    }

    @Test
    void convert_StatusIsAlwaysActive_ShouldReturnDtoWithActiveStatus() {
        Long expectedId = 7L;
        String expectedText = "Another test item";

        when(shoppingListItemTranslation.getShoppingListItem()).thenReturn(shoppingListItem);
        when(shoppingListItem.getId()).thenReturn(expectedId);
        when(shoppingListItemTranslation.getContent()).thenReturn(expectedText);

        ShoppingListItemDto result = shoppingListItemDtoMapper.convert(shoppingListItemTranslation);

        assertNotNull(result);
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), result.getStatus());
    }

    @Test
    void convert_VerifyMethodCalls_ShouldCallAppropriateGetterMethods() {
        Long expectedId = 8L;
        String expectedText = "Verify method calls";

        when(shoppingListItemTranslation.getShoppingListItem()).thenReturn(shoppingListItem);
        when(shoppingListItem.getId()).thenReturn(expectedId);
        when(shoppingListItemTranslation.getContent()).thenReturn(expectedText);

        ShoppingListItemDto result = shoppingListItemDtoMapper.convert(shoppingListItemTranslation);

        verify(shoppingListItemTranslation, times(1)).getShoppingListItem();
        verify(shoppingListItem, times(1)).getId();
        verify(shoppingListItemTranslation, times(1)).getContent();
        assertNotNull(result);
        assertEquals(expectedId, result.getId());
        assertEquals(expectedText, result.getText());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), result.getStatus());
    }
}
