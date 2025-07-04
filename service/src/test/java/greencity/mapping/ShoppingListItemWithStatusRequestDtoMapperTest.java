package greencity.mapping;

import greencity.dto.shoppinglistitem.ShoppingListItemWithStatusRequestDto;
import greencity.entity.ShoppingListItem;
import greencity.entity.UserShoppingListItem;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Mykyta Sirobaba on 05.07.2025.
 * email mykyta.sirobaba@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class ShoppingListItemWithStatusRequestDtoMapperTest {

    @InjectMocks
    private ShoppingListItemWithStatusRequestDtoMapper shoppingListItemWithStatusRequestDtoMapper;

    @Test
    void convert() {
        ShoppingListItemWithStatusRequestDto shoppingListItemWithStatusRequestDto =
                ShoppingListItemWithStatusRequestDto
                        .builder()
                        .id(1L)
                        .status(ShoppingListItemStatus.DONE)
                        .build();

        UserShoppingListItem expected =
                UserShoppingListItem
                        .builder()
                        .shoppingListItem(ShoppingListItem.builder()
                                .id(shoppingListItemWithStatusRequestDto.getId())
                                .build())
                        .status(shoppingListItemWithStatusRequestDto.getStatus())
                        .build();

        UserShoppingListItem actual = shoppingListItemWithStatusRequestDtoMapper.convert(shoppingListItemWithStatusRequestDto);

        assertEquals(expected.getShoppingListItem().getId(), actual.getShoppingListItem().getId());
        assertEquals(expected.getStatus(), actual.getStatus());
    }
}