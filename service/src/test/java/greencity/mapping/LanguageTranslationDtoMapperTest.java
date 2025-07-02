package greencity.mapping;

import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFact;
import greencity.entity.HabitFactTranslation;
import greencity.entity.Language;
import greencity.enums.FactOfDayStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LanguageTranslationDtoMapperTest {

    @InjectMocks
    LanguageTranslationDtoMapper languageTranslationDtoMapper;

    @Test
    @DisplayName("Should convert HabitFactTranslation to LanguageTranslationDTO")
    void shouldConvertHabitFactTranslationToLanguageTranslationDTO() {
        HabitFactTranslation habitFactTranslation = HabitFactTranslation.builder()
                .id(1L)
                .content("Interesting fact")
                .language(Language.builder().code("en").build())
                .habitFact(new HabitFact())
                .factOfDayStatus(FactOfDayStatus.CURRENT)
                .build();

        LanguageTranslationDTO languageTranslationDTOExpected = LanguageTranslationDTO.builder()
                .content("Interesting fact")
                .language(LanguageDTO.builder().code("en").build())
                .build();

        LanguageTranslationDTO result = languageTranslationDtoMapper.convert(habitFactTranslation);

        assertThat(result).usingRecursiveComparison().isEqualTo(languageTranslationDTOExpected);
    }
}