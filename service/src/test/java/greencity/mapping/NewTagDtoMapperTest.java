package greencity.mapping;

import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import greencity.entity.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewTagDtoMapperTest {

    @InjectMocks
    private NewTagDtoMapper mapper;

    private Tag tag;
    private Language englishLanguage;
    private Language ukrainianLanguage;

    @BeforeEach
    void setUp() {
        englishLanguage = Language.builder()
                .code("en")
                .build();

        ukrainianLanguage = Language.builder()
                .code("ua")
                .build();

        tag = Tag.builder()
                .id(1L)
                .build();
    }

    @Test
    void convert_WithBothEnglishAndUkrainianTranslations_ShouldMapCorrectly() {
        TagTranslation englishTranslation = TagTranslation.builder()
                .name("Environment")
                .language(englishLanguage)
                .tag(tag)
                .build();
        TagTranslation ukrainianTranslation = TagTranslation.builder()
                .name("Довкілля")
                .language(ukrainianLanguage)
                .tag(tag)
                .build();
        tag.setTagTranslations(Arrays.asList(englishTranslation, ukrainianTranslation));

        NewTagDto result = mapper.convert(tag);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Environment", result.getName());
        assertEquals("Довкілля", result.getNameUa());
    }

    @Test
    void convert_WithOnlyEnglishTranslation_ShouldMapEnglishAndNullUkrainian() {
        TagTranslation englishTranslation = TagTranslation.builder()
                .name("Green Energy")
                .language(englishLanguage)
                .tag(tag)
                .build();
        tag.setTagTranslations(Collections.singletonList(englishTranslation));

        NewTagDto result = mapper.convert(tag);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Green Energy", result.getName());
        assertNull(result.getNameUa());
    }

    @Test
    void convert_WithOnlyUkrainianTranslation_ShouldMapUkrainianAndNullEnglish() {
        TagTranslation ukrainianTranslation = TagTranslation.builder()
                .name("Екологія")
                .language(ukrainianLanguage)
                .tag(tag)
                .build();
        tag.setTagTranslations(Collections.singletonList(ukrainianTranslation));

        NewTagDto result = mapper.convert(tag);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertEquals("Екологія", result.getNameUa());
    }

    @Test
    void convert_WithNoTranslations_ShouldMapWithNullNames() {
        tag.setTagTranslations(Collections.emptyList());

        NewTagDto result = mapper.convert(tag);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertNull(result.getNameUa());
    }

    @Test
    void convert_WithMultipleTranslationsInSameLanguage_ShouldTakeFirst() {
        TagTranslation englishTranslation1 = TagTranslation.builder()
                .name("First English")
                .language(englishLanguage)
                .tag(tag)
                .build();
        TagTranslation englishTranslation2 = TagTranslation.builder()
                .name("Second English")
                .language(englishLanguage)
                .tag(tag)
                .build();
        tag.setTagTranslations(Arrays.asList(englishTranslation1, englishTranslation2));

        NewTagDto result = mapper.convert(tag);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("First English", result.getName());
    }

    @Test
    void convert_WithOtherLanguageTranslations_ShouldIgnoreNonEnglishNonUkrainian() {
        Language germanLanguage = Language.builder()
                .code("de")
                .build();
        TagTranslation germanTranslation = TagTranslation.builder()
                .name("Umwelt")
                .language(germanLanguage)
                .tag(tag)
                .build();
        TagTranslation englishTranslation = TagTranslation.builder()
                .name("Environment")
                .language(englishLanguage)
                .tag(tag)
                .build();
        tag.setTagTranslations(Arrays.asList(germanTranslation, englishTranslation));

        NewTagDto result = mapper.convert(tag);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Environment", result.getName());
        assertNull(result.getNameUa());
    }

    @Test
    void convert_WithNullTagTranslations_ShouldHandleGracefully() {
        tag.setTagTranslations(null);

        assertThrows(NullPointerException.class, () -> mapper.convert(tag));
    }

    @Test
    void convert_WithTranslationHavingNullLanguage_ShouldHandleGracefully() {
        TagTranslation translationWithNullLanguage = TagTranslation.builder()
                .name("Some name")
                .language(null)
                .tag(tag)
                .build();
        tag.setTagTranslations(Collections.singletonList(translationWithNullLanguage));

        assertThrows(NullPointerException.class, () -> mapper.convert(tag));
    }

    @Test
    void convert_WithTranslationHavingNullLanguageCode_ShouldHandleGracefully() {
        Language languageWithNullCode = Language.builder()
                .code(null)
                .build();
        TagTranslation translation = TagTranslation.builder()
                .name("Some name")
                .language(languageWithNullCode)
                .tag(tag)
                .build();
        tag.setTagTranslations(Collections.singletonList(translation));

        assertThrows(NullPointerException.class, () -> mapper.convert(tag));
    }
}