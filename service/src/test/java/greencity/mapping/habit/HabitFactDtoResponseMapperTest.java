package greencity.mapping.habit;

import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageVO;
import greencity.enums.FactOfDayStatus;
import greencity.mapping.HabitFactDtoResponseMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HabitFactDtoResponseMapperTest {

    @Test
    void habitFactDtoResponseMapperTest() {

        LanguageVO languageVO = mock(LanguageVO.class);
        when(languageVO.getId()).thenReturn(1L);
        when(languageVO.getCode()).thenReturn("en");

        HabitVO habitVO = mock(HabitVO.class);
        when(habitVO.getId()).thenReturn(1L);
        when(habitVO.getImage()).thenReturn("habitVoImage");
        when(habitVO.getComplexity()).thenReturn(1);

        HabitFactTranslationVO habitFactTranslationVO = mock(HabitFactTranslationVO.class);
        when(habitFactTranslationVO.getId()).thenReturn(10L);
        when(habitFactTranslationVO.getContent()).thenReturn("habitFactTranslationContent");
        when(habitFactTranslationVO.getFactOfDayStatus()).thenReturn(FactOfDayStatus.POTENTIAL);

        var language = mock(LanguageDTO.class);
        when(language.getId()).thenReturn(1L);
        when(language.getCode()).thenReturn("en");
        when(habitFactTranslationVO.getLanguage()).thenReturn(languageVO);

        HabitFactVO habitFactVO = mock(HabitFactVO.class);
        when(habitFactVO.getId()).thenReturn(1L);
        when(habitFactVO.getHabit()).thenReturn(habitVO);
        when(habitFactVO.getTranslations()).thenReturn(List.of(habitFactTranslationVO));

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
