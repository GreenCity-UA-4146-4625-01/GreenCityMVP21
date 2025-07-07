package greencity.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

    private final ImageValidator validator = new ImageValidator();

    @Test
    void testIsValid_NullFile_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/png", "image/jpeg", "image/jpg"})
    void testIsValid_ValidImageTypes_ReturnsTrue(String contentType) {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(contentType);

        assertTrue(validator.isValid(file, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"image/gif"})
    @NullSource
    void testIsValid_InvalidContentTypes_ReturnsFalse(String contentType) {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(contentType);

        assertFalse(validator.isValid(file, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "image/", "IMAGE/PNG", "image/jpeg; charset=utf-8"})
    void testIsValid_EdgeCases_ReturnsFalse(String contentType) {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(contentType);

        assertFalse(validator.isValid(file, null));
    }

}
