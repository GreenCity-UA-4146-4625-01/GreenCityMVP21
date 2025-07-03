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

class HabitManagementDtoMapperTest {

    @Test
    void convertTest() {

        Language language = Language.builder()
                .code("en")
                .build();

        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .description("description")
                .habitItem("habitItem")
                .name("habitName")
                .language(language)
                .build();

        List<HabitTranslation> habitTranslations = new LinkedList<>();
        habitTranslations.add(habitTranslation);

        Habit habit = Habit.builder()
                .id(1L)
                .image("image")
                .defaultDuration(10)
                .habitTranslations(habitTranslations)
                .build();

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
