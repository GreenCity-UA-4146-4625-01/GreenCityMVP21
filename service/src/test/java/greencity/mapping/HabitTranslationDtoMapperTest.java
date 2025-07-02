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
class HabitTranslationDtoMapperTest {

    @InjectMocks
    HabitTranslationDtoMapper habitTranslationDtoMapper;

    @Test
    @DisplayName("Should convert HabitTranslation to HabitTranslationDto")
    void shouldConvertHabitTranslationToHabitTranslationDto() {
        HabitTranslation habitTranslation = HabitTranslation.builder()
                .id(1L)
                .name("Plastic bag")
                .habit(new Habit())
                .habitItem("Item")
                .language(Language.builder().code("en").build())
                .description("Description")
                .build();

        HabitTranslationDto habitTranslationDtoExpected = HabitTranslationDto.builder()
                .name("Plastic bag")
                .habitItem("Item")
                .description("Description")
                .languageCode("en")
                .build();

        HabitTranslationDto result = habitTranslationDtoMapper.convert(habitTranslation);

        assertThat(result).usingRecursiveComparison().isEqualTo(habitTranslationDtoExpected);
    }

    @Test
    @DisplayName("Should build list of HabitTranslationDto from HabitTranslation")
    void shouldBuildHabitTranslationDtoListFromHabitTranslationList() {
        HabitTranslation habitTranslation1 = HabitTranslation.builder()
                .id(1L)
                .name("Plastic bag")
                .habit(new Habit())
                .habitItem("Item")
                .language(Language.builder().code("en").build())
                .description("Description")
                .build();

        HabitTranslation habitTranslation2 = HabitTranslation.builder()
                .id(2L)
                .name("Plastic gloves")
                .habit(new Habit())
                .habitItem("Item")
                .language(Language.builder().code("en").build())
                .description("Description")
                .build();

        HabitTranslationDto habitTranslationDto1Expected = HabitTranslationDto.builder()
                .name("Plastic bag")
                .habitItem("Item")
                .description("Description")
                .languageCode("en")
                .build();

        HabitTranslationDto habitTranslationDto2Expected = HabitTranslationDto.builder()
                .name("Plastic gloves")
                .habitItem("Item")
                .description("Description")
                .languageCode("en")
                .build();

        List<HabitTranslation> habitTranslationList = List.of(habitTranslation1, habitTranslation2);
        List<HabitTranslationDto> habitTranslationDtoListExpected = List.of(habitTranslationDto1Expected, habitTranslationDto2Expected);

        List<HabitTranslationDto> result = habitTranslationDtoMapper.mapAllToList(habitTranslationList);

        assertThat(result).usingRecursiveComparison().isEqualTo(habitTranslationDtoListExpected);
    }
}