package greencity.mapping.habit;

import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageVO;
import greencity.enums.FactOfDayStatus;
import greencity.mapping.HabitFactDtoResponseMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HabitFactDtoResponseMapperTest {

    @Test
    void habitFactDtoResponseMapperTest() {

        LanguageVO languageVO = LanguageVO.builder()
                .id(1L)
                .code("en")
                .build();

        HabitVO habitVO = HabitVO.builder()
                .id(1L)
                .image("habitVoImage")
                .complexity(1)
                .build();

        HabitFactTranslationVO habitFactTranslationVO = HabitFactTranslationVO.builder()
                .id(10L)
                .content("habitFactTranslationContent")
                .factOfDayStatus(FactOfDayStatus.POTENTIAL)
                .language(languageVO)
                .build();

        HabitFactVO habitFactVO = HabitFactVO.builder()
                .id(1L)
                .habit(habitVO)
                .translations(List.of(habitFactTranslationVO))
                .build();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new HabitFactDtoResponseMapper());

        HabitFactDtoResponse response = modelMapper.map(habitFactVO, HabitFactDtoResponse.class);

        assertEquals(1L, languageVO.getId());
        assertEquals(habitVO, response.getHabit());

        assertNotNull(response.getTranslations());
        assertEquals(1L, response.getTranslations().size());

        var translated = response.getTranslations().get(0);
        assertEquals(10L, translated.getId());
        assertEquals("habitFactTranslationContent", translated.getContent());
        assertEquals(FactOfDayStatus.POTENTIAL, translated.getFactOfDayStatus());

        assertNotNull(translated.getLanguage());
        assertEquals(1L, translated.getLanguage().getId());
        assertEquals("en", translated.getLanguage().getCode());
    }
}
