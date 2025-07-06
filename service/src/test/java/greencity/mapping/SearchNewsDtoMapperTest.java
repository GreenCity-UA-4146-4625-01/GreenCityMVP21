package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchNewsDtoMapperTest {

    @InjectMocks
    private SearchNewsDtoMapper searchNewsDtoMapper;

    private EcoNews ecoNews;
    private User author;
    private Tag tag1;
    private Tag tag2;
    private Language englishLanguage;
    private Language ukrainianLanguage;
    private TagTranslation englishTagTranslation1;
    private TagTranslation ukrainianTagTranslation1;
    private TagTranslation englishTagTranslation2;
    private TagTranslation ukrainianTagTranslation2;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(1L)
                .name("John Doe")
                .build();

        englishLanguage = Language.builder()
                .id(1L)
                .code("en")
                .build();
        ukrainianLanguage = Language.builder()
                .id(2L)
                .code("uk")
                .build();

        tag1 = Tag.builder()
                .id(1L)
                .tagTranslations(new ArrayList<>())
                .build();
        tag2 = Tag.builder()
                .id(2L)
                .tagTranslations(new ArrayList<>())
                .build();

        englishTagTranslation1 = TagTranslation.builder()
                .id(1L)
                .name("Environment")
                .tag(tag1)
                .language(englishLanguage)
                .build();
        ukrainianTagTranslation1 = TagTranslation.builder()
                .id(2L)
                .name("Довкілля")
                .tag(tag1)
                .language(ukrainianLanguage)
                .build();
        englishTagTranslation2 = TagTranslation.builder()
                .id(3L)
                .name("Climate")
                .tag(tag2)
                .language(englishLanguage)
                .build();
        ukrainianTagTranslation2 = TagTranslation.builder()
                .id(4L)
                .name("Клімат")
                .tag(tag2)
                .language(ukrainianLanguage)
                .build();

        tag1.getTagTranslations().add(englishTagTranslation1);
        tag1.getTagTranslations().add(ukrainianTagTranslation1);
        tag2.getTagTranslations().add(englishTagTranslation2);
        tag2.getTagTranslations().add(ukrainianTagTranslation2);

        ecoNews = EcoNews.builder()
                .id(1L)
                .title("Climate Change Article")
                .author(author)
                .creationDate(ZonedDateTime.now())
                .tags(List.of(tag1, tag2))
                .build();
    }

    @Test
    void convert_WithEnglishLocale_ShouldReturnSearchNewsDtoWithEnglishTags() {
        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(Locale.ENGLISH);

            SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

            assertNotNull(result);
            assertEquals(ecoNews.getId(), result.getId());
            assertEquals(ecoNews.getTitle(), result.getTitle());
            assertEquals(ecoNews.getCreationDate(), result.getCreationDate());

            EcoNewsAuthorDto authorDto = result.getAuthor();
            assertNotNull(authorDto);
            assertEquals(author.getId(), authorDto.getId());
            assertEquals(author.getName(), authorDto.getName());

            List<String> tags = result.getTags();
            assertNotNull(tags);
            assertEquals(2, tags.size());
            assertTrue(tags.contains("Environment"));
            assertTrue(tags.contains("Climate"));
        }
    }

    @Test
    void convert_WithUkrainianLocale_ShouldReturnSearchNewsDtoWithUkrainianTags() {
        Locale ukrainianLocale = Locale.forLanguageTag("uk");
        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(ukrainianLocale);

            SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

            assertNotNull(result);
            assertEquals(ecoNews.getId(), result.getId());
            assertEquals(ecoNews.getTitle(), result.getTitle());
            assertEquals(ecoNews.getCreationDate(), result.getCreationDate());

            EcoNewsAuthorDto authorDto = result.getAuthor();
            assertNotNull(authorDto);
            assertEquals(author.getId(), authorDto.getId());
            assertEquals(author.getName(), authorDto.getName());

            List<String> tags = result.getTags();
            assertNotNull(tags);
            assertEquals(2, tags.size());
            assertTrue(tags.contains("Довкілля"));
            assertTrue(tags.contains("Клімат"));
        }
    }

    @Test
    void convert_WithEmptyTags_ShouldReturnSearchNewsDtoWithEmptyTagsList() {
        ecoNews.setTags(new ArrayList<>());
        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(Locale.ENGLISH);

            SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

            assertNotNull(result);
            assertEquals(ecoNews.getId(), result.getId());
            assertEquals(ecoNews.getTitle(), result.getTitle());
            assertEquals(ecoNews.getCreationDate(), result.getCreationDate());

            List<String> tags = result.getTags();
            assertNotNull(tags);
            assertTrue(tags.isEmpty());
        }
    }

    @Test
    void convert_WithNoMatchingLanguageTranslations_ShouldReturnEmptyTagsList() {
        Locale germanLocale = Locale.forLanguageTag("de");
        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(germanLocale);

            SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

            assertNotNull(result);
            assertEquals(ecoNews.getId(), result.getId());
            assertEquals(ecoNews.getTitle(), result.getTitle());
            assertEquals(ecoNews.getCreationDate(), result.getCreationDate());

            List<String> tags = result.getTags();
            assertNotNull(tags);
            assertTrue(tags.isEmpty());
        }
    }

    @Test
    void convert_WithNullTags_ShouldHandleGracefully() {
        ecoNews.setTags(null);
        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(Locale.ENGLISH);

            assertThrows(NullPointerException.class, () -> searchNewsDtoMapper.convert(ecoNews));
        }
    }

    @Test
    void convert_WithTagsHavingNoTranslations_ShouldReturnEmptyTagsList() {
        Tag emptyTag = Tag.builder()
                .id(3L)
                .tagTranslations(new ArrayList<>())
                .build();
        ecoNews.setTags(List.of(emptyTag));
        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(Locale.ENGLISH);

            SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

            assertNotNull(result);
            List<String> tags = result.getTags();
            assertNotNull(tags);
            assertTrue(tags.isEmpty());
        }
    }

    @Test
    void convert_WithMixedLanguageTranslations_ShouldReturnOnlyMatchingLanguage() {
        Tag ukrainianOnlyTag = Tag.builder()
                .id(4L)
                .tagTranslations(new ArrayList<>())
                .build();
        TagTranslation ukrainianOnlyTranslation = TagTranslation.builder()
                .id(5L)
                .name("Тільки українська")
                .tag(ukrainianOnlyTag)
                .language(ukrainianLanguage)
                .build();

        ukrainianOnlyTag.getTagTranslations().add(ukrainianOnlyTranslation);

        ecoNews.setTags(List.of(tag1, tag2, ukrainianOnlyTag));

        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(Locale.ENGLISH);
            SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

            assertNotNull(result);
            List<String> tags = result.getTags();
            assertNotNull(tags);
            assertEquals(2, tags.size());
            assertTrue(tags.contains("Environment"));
            assertTrue(tags.contains("Climate"));
            assertFalse(tags.contains("Тільки українська"));
        }
    }

    @Test
    void convert_WithNullAuthor_ShouldThrowNullPointerException() {
        ecoNews.setAuthor(null);
        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(Locale.ENGLISH);

            assertThrows(NullPointerException.class, () -> searchNewsDtoMapper.convert(ecoNews));
        }
    }

    @Test
    void convert_WithValidData_ShouldPreserveAllFields() {
        ZonedDateTime expectedDate = ZonedDateTime.now();
        ecoNews.setCreationDate(expectedDate);

        try (MockedStatic<LocaleContextHolder> mockedLocaleContext = mockStatic(LocaleContextHolder.class)) {
            mockedLocaleContext.when(LocaleContextHolder::getLocale).thenReturn(Locale.ENGLISH);

            SearchNewsDto result = searchNewsDtoMapper.convert(ecoNews);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Climate Change Article", result.getTitle());
            assertEquals(expectedDate, result.getCreationDate());

            EcoNewsAuthorDto authorDto = result.getAuthor();
            assertNotNull(authorDto);
            assertEquals(1L, authorDto.getId());
            assertEquals("John Doe", authorDto.getName());

            List<String> tags = result.getTags();
            assertNotNull(tags);
            assertEquals(2, tags.size());
        }
    }
}