package greencity.validator;

import greencity.service.LanguageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class LanguageValidatorTest {
    @InjectMocks
    private LanguageValidator languageValidator;
    @Mock
    private LanguageService languageService;

    @Test
    void isValidTrueTest() {
        List<String> languageCodes = Arrays.asList("en", "fr", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(languageCodes);

        languageValidator.initialize(null);
        assertTrue(languageValidator.isValid(Locale.FRENCH, null));
    }

    @Test
    void isValidFalseTest() {
        List<String> languageCodes = Arrays.asList("en", "ua");
        when(languageService.findAllLanguageCodes()).thenReturn(languageCodes);

        languageValidator.initialize(null);
        assertFalse(languageValidator.isValid(Locale.FRENCH, null));
    }
}
