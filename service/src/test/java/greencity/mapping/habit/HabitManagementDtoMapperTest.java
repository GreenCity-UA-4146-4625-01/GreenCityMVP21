package greencity.mapping.habit;

import greencity.dto.habit.HabitManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.mapping.HabitManagementDtoMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HabitManagementDtoMapperTest {

    @Test
    void convertTest() {

        Language language = mock(Language.class);
        when(language.getCode()).thenReturn("en");

        HabitTranslation habitTranslation = mock(HabitTranslation.class);
        when(habitTranslation.getId()).thenReturn(1L);
        when(habitTranslation.getDescription()).thenReturn("description");
        when(habitTranslation.getHabitItem()).thenReturn("habitItem");
        when(habitTranslation.getName()).thenReturn("habitName");
        when(habitTranslation.getLanguage()).thenReturn(language);

        Habit habit = mock(Habit.class);
        when(habit.getId()).thenReturn(1L);
        when(habit.getImage()).thenReturn("image");
        when(habit.getDefaultDuration()).thenReturn(10);

        List<HabitTranslation> habitTranslations = new LinkedList<>();
        habitTranslations.add(habitTranslation);

        when(habit.getHabitTranslations()).thenReturn(habitTranslations);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new HabitManagementDtoMapper());

        HabitManagementDto habitManagementDto = modelMapper.map(habit, HabitManagementDto.class);

        assertEquals(1L, habitManagementDto.getId());
        assertNotNull(habitManagementDto);

        assertEquals("image", habitManagementDto.getImage());
        assertEquals(10, habitManagementDto.getDefaultDuration());
        assertEquals(1L, habitManagementDto.getHabitTranslations().getFirst().getId());

        assertEquals("habitName", habitManagementDto.getHabitTranslations().getFirst().getName());
        assertEquals("description", habitManagementDto.getHabitTranslations().getFirst().getDescription());
    }
}
