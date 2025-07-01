package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.entity.CustomShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CustomShoppingListMapperTest {
    @InjectMocks
    CustomShoppingListMapper customShoppingListMapper;

    @Test
    void convertSuccess() {
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto = new CustomShoppingListItemResponseDto();
        customShoppingListItemResponseDto.setId(1L);
        customShoppingListItemResponseDto.setText("testText");
        customShoppingListItemResponseDto.setStatus(ShoppingListItemStatus.ACTIVE);

        CustomShoppingListItem expectedCustomShoppingListItem =
            CustomShoppingListItem.builder()
                .id(customShoppingListItemResponseDto.getId())
                .text(customShoppingListItemResponseDto.getText())
                .status(customShoppingListItemResponseDto.getStatus())
                .build();

        assertEquals(expectedCustomShoppingListItem,
            customShoppingListMapper.convert(customShoppingListItemResponseDto));
        assertEquals(expectedCustomShoppingListItem.getStatus(),
            customShoppingListMapper.convert(customShoppingListItemResponseDto).getStatus());
        assertEquals(expectedCustomShoppingListItem.getText(),
            customShoppingListMapper.convert(customShoppingListItemResponseDto).getText());
        assertEquals(expectedCustomShoppingListItem.getId(),
            customShoppingListMapper.convert(customShoppingListItemResponseDto).getId());
    }

    @Test
    void convertWithNullFields() {
        CustomShoppingListItemResponseDto customShoppingListItemResponseDto = new CustomShoppingListItemResponseDto();

        assertNull(customShoppingListMapper.convert(customShoppingListItemResponseDto).getText());
        assertNull(customShoppingListMapper.convert(customShoppingListItemResponseDto).getId());
        assertNull(customShoppingListMapper.convert(customShoppingListItemResponseDto).getStatus());
    }

    @Test
    void mapAllToListSuccess() {
        List<CustomShoppingListItemResponseDto> items = List.of(
            CustomShoppingListItemResponseDto.builder()
                .id(1L)
                .text("Test1")
                .status(ShoppingListItemStatus.ACTIVE)
                .build(),
            CustomShoppingListItemResponseDto.builder()
                .id(2L)
                .text("Test2")
                .status(ShoppingListItemStatus.ACTIVE)
                .build());

        List<CustomShoppingListItem> dtos = customShoppingListMapper.mapAllToList(items);

        assertEquals(2, dtos.size());
        assertEquals("Test1", dtos.get(0).getText());
        assertEquals(1L, dtos.get(0).getId());
        assertEquals("Test2", dtos.get(1).getText());
        assertEquals(ShoppingListItemStatus.ACTIVE, dtos.get(1).getStatus());
    }
}
