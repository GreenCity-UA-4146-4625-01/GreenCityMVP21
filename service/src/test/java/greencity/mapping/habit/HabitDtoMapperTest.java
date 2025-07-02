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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HabitDtoMapperTest {

    @Test
    void convertTest() {

        Language language = mock(Language.class);
        when(language.getCode()).thenReturn("en");

        TagTranslation tagTranslation = mock(TagTranslation.class);
        when(tagTranslation.getLanguage()).thenReturn(language);
        when(tagTranslation.getName()).thenReturn("tagName");

        Tag tag = mock(Tag.class);
        when(tag.getTagTranslations()).thenReturn(List.of(tagTranslation));

        ShoppingListItemTranslation shoppingListItemTranslation = mock(ShoppingListItemTranslation.class);
        when(shoppingListItemTranslation.getLanguage()).thenReturn(language);
        when(shoppingListItemTranslation.getContent()).thenReturn("shopping list item content");

        ShoppingListItem shoppingListItem = mock(ShoppingListItem.class);
        when(shoppingListItem.getId()).thenReturn(1L);
        when(shoppingListItem.getTranslations()).thenReturn(List.of(shoppingListItemTranslation));

        Habit habit = mock(Habit.class);
        when(habit.getId()).thenReturn(1L);
        when(habit.getImage()).thenReturn("image");
        when(habit.getDefaultDuration()).thenReturn(15);
        when(habit.getTags()).thenReturn(Set.of(tag));
        when(habit.getShoppingListItems()).thenReturn(Set.of(shoppingListItem));

        HabitTranslation habitTranslation = mock(HabitTranslation.class);
        when(habitTranslation.getLanguage()).thenReturn(language);
        when(habitTranslation.getHabit()).thenReturn(habit);
        when(habitTranslation.getHabit().getComplexity()).thenReturn(3);
        when(habitTranslation.getDescription()).thenReturn("habit description");
        when(habitTranslation.getHabitItem()).thenReturn("habitItem");
        when(habitTranslation.getName()).thenReturn("habitName");

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
        assertEquals("shopping list item content", habitDto.getShoppingListItems().get(0).getText());
    }
}
