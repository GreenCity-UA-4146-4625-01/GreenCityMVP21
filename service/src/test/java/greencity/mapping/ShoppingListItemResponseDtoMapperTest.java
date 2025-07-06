package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.shoppinglistitem.ShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.ShoppingListItemTranslationDTO;
import greencity.entity.ShoppingListItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Mykyta Sirobaba on 05.07.2025.
 * email mykyta.sirobaba@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class ShoppingListItemResponseDtoMapperTest {

    @InjectMocks
    private ShoppingListItemResponseDtoMapper shoppingListItemResponseDtoMapper;

    @Test
    void convert() {
        ShoppingListItem shoppingListItem = ModelUtils.getShoppingListItem();

        ShoppingListItemResponseDto expected = ShoppingListItemResponseDto.builder()
                .id(shoppingListItem.getId())
                .translations(shoppingListItem.getTranslations().stream().map(
                                shoppingListItemTranslation -> ShoppingListItemTranslationDTO.builder()
                                        .id(shoppingListItemTranslation.getId())
                                        .content(shoppingListItemTranslation.getContent())
                                        .build())
                        .collect(Collectors.toList()))
                .build();

        assertEquals(expected, shoppingListItemResponseDtoMapper.convert(shoppingListItem));
    }
}