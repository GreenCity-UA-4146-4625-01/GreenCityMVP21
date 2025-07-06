package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HabitTranslationMapperTest {

    @InjectMocks
    HabitTranslationMapper habitTranslationMapper;

    @Test
    @DisplayName("Should convert HabitTranslationDto to HabitTranslation")
    void shouldConvertHabitTranslationDtoToHabitTranslation() {
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
                .name("Plastic bag")
                .habitItem("Item")
                .description("Description")
                .languageCode("en")
                .build();

        HabitTranslation habitTranslationExpected = HabitTranslation.builder()
                .id(1L)
                .name("Plastic bag")
                .habit(new Habit())
                .habitItem("Item")
                .language(Language.builder().code("en").build())
                .description("Description")
                .build();

        HabitTranslation result = habitTranslationMapper.convert(habitTranslationDto);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id", "habit", "language")
                .isEqualTo(habitTranslationExpected);
    }

    @Test
    @DisplayName("Should build list of HabitTranslation from HabitTranslationDto")
    void shouldBuildHabitTranslationListFromHabitTranslationDtoList() {
        HabitTranslationDto habitTranslationDto1 = HabitTranslationDto.builder()
                .name("Plastic bag")
                .habitItem("Item")
                .description("Description")
                .languageCode("en")
                .build();

        HabitTranslationDto habitTranslationDto2 = HabitTranslationDto.builder()
                .name("Plastic gloves")
                .habitItem("Item")
                .description("Description")
                .languageCode("en")
                .build();

        HabitTranslation habitTranslation1Expected = HabitTranslation.builder()
                .id(1L)
                .name("Plastic bag")
                .habit(new Habit())
                .habitItem("Item")
                .language(Language.builder().code("en").build())
                .description("Description")
                .build();

        HabitTranslation habitTranslation2Expected = HabitTranslation.builder()
                .id(2L)
                .name("Plastic gloves")
                .habit(new Habit())
                .habitItem("Item")
                .language(Language.builder().code("en").build())
                .description("Description")
                .build();

        List<HabitTranslationDto> habitTranslationDtoList = List.of(habitTranslationDto1, habitTranslationDto2);
        List<HabitTranslation> habitTranslationListExpected = List.of(habitTranslation1Expected, habitTranslation2Expected);

        List<HabitTranslation> result = habitTranslationMapper.mapAllToList(habitTranslationDtoList);

        assertThat(result).usingRecursiveComparison()
                .ignoringFields("id", "habit", "language")
                .isEqualTo(habitTranslationListExpected);
    }
}