package greencity.mapping.habit;

import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.*;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.ShoppingListItemStatus;
import greencity.mapping.HabitDtoMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HabitDtoMapperTest {

    @Test
    void convertTest() {

        Language language = Language.builder()
                .code("en")
                .build();

        Tag tag = Tag.builder()
                .tagTranslations(List.of(
                        TagTranslation.builder()
                                .language(language)
                                .name("tagName")
                                .build()
                ))
                .build();

        ShoppingListItem shoppingListItem = ShoppingListItem.builder()
                .id(1L)
                .translations(List.of(
                        ShoppingListItemTranslation.builder()
                                .language(language)
                                .content("content")
                                .build()
                ))
                .build();

        HabitTranslation habitTranslation = HabitTranslation.builder()
                .language(language)
                .habit(Habit.builder()
                        .id(1L)
                        .image("image")
                        .defaultDuration(15)
                        .complexity(3)
                        .tags(Set.of(tag))
                        .shoppingListItems(Set.of(shoppingListItem))
                        .build())
                .description("habit description")
                .habitItem("habitItem")
                .name("habitName")
                .build();

        ModelMapper modelMapper = new ModelMapper();
        HabitDtoMapper mapper = new HabitDtoMapper();
        modelMapper.addConverter(mapper);

        HabitDto habitDto = modelMapper.map(habitTranslation, HabitDto.class);

        assertEquals(1L, habitDto.getId());
        assertEquals("image", habitDto.getImage());
        assertEquals(15, habitDto.getDefaultDuration());
        assertEquals(3, habitDto.getComplexity());

        HabitTranslationDto htDto = habitDto.getHabitTranslation();
        assertNotNull(htDto);
        assertEquals("habit description", htDto.getDescription());
        assertEquals("habitName", htDto.getName());
        assertEquals("en", habitDto.getHabitTranslation().getLanguageCode());

        assertNotNull(habitDto.getTags());
        assertEquals(1, habitDto.getTags().size());
        assertEquals("tagName", habitDto.getTags().get(0));

        assertNotNull(habitDto.getShoppingListItems());
        assertEquals(1, habitDto.getShoppingListItems().size());
        assertEquals(1L, habitDto.getShoppingListItems().get(0).getId());
        assertEquals(ShoppingListItemStatus.ACTIVE.toString(), habitDto.getShoppingListItems().get(0).getStatus());
        assertEquals("content", habitDto.getShoppingListItems().get(0).getText());
    }
}
