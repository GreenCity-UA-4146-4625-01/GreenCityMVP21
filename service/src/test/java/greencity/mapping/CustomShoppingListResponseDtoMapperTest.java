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
public class CustomShoppingListResponseDtoMapperTest {
    @InjectMocks
    CustomShoppingListResponseDtoMapper customShoppingListResponseDtoMapper;

    @Test
    void convertSuccess() {
        CustomShoppingListItem customShoppingListItem = new CustomShoppingListItem();
        customShoppingListItem.setId(1L);
        customShoppingListItem.setText("testText");
        customShoppingListItem.setStatus(ShoppingListItemStatus.ACTIVE);

        CustomShoppingListItemResponseDto expectedCustomShoppingListItemResponseDto =
                CustomShoppingListItemResponseDto.builder()
                        .id(customShoppingListItem.getId())
                        .text(customShoppingListItem.getText())
                        .status(customShoppingListItem.getStatus())
                        .build();

        assertEquals(expectedCustomShoppingListItemResponseDto, customShoppingListResponseDtoMapper.convert(customShoppingListItem));
        assertEquals(expectedCustomShoppingListItemResponseDto.getStatus(), customShoppingListResponseDtoMapper.convert(customShoppingListItem).getStatus());
        assertEquals(expectedCustomShoppingListItemResponseDto.getText(), customShoppingListResponseDtoMapper.convert(customShoppingListItem).getText());
        assertEquals(expectedCustomShoppingListItemResponseDto.getId(), customShoppingListResponseDtoMapper.convert(customShoppingListItem).getId());
    }

    @Test
    void convertWithNullFields() {
        CustomShoppingListItem customShoppingListItem = new CustomShoppingListItem();

        assertNull(customShoppingListResponseDtoMapper.convert(customShoppingListItem).getText());
        assertNull(customShoppingListResponseDtoMapper.convert(customShoppingListItem).getId());
        assertEquals(ShoppingListItemStatus.ACTIVE, customShoppingListResponseDtoMapper.convert(customShoppingListItem).getStatus());
    }

    @Test
    void mapAllToListSuccess() {
        List<CustomShoppingListItem> items = List.of(
                CustomShoppingListItem.builder().id(1L).text("Test1").status(ShoppingListItemStatus.ACTIVE).build(),
                CustomShoppingListItem.builder().id(2L).text("Test2").status(ShoppingListItemStatus.ACTIVE).build()
        );

        List<CustomShoppingListItemResponseDto> dtos = customShoppingListResponseDtoMapper.mapAllToList(items);

        assertEquals(2, dtos.size());
        assertEquals("Test1", dtos.get(0).getText());
        assertEquals(1L, dtos.get(0).getId());
    }
}
