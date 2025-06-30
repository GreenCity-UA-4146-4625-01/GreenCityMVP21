package greencity.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

    private final ImageValidator validator = new ImageValidator();

    @Test
    void testIsValid_NullFile_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void testIsValid_Png_ReturnsTrue() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn("image/png");

        assertTrue(validator.isValid(file, null));
    }

    @Test
    void testIsValid_Jpeg_ReturnsTrue() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn("image/jpeg");

        assertTrue(validator.isValid(file, null));
    }

    @Test
    void testIsValid_Jpg_ReturnsTrue() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn("image/jpg");

        assertTrue(validator.isValid(file, null));
    }

    @Test
    void testIsValid_Gif_ReturnsFalse() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn("image/gif");

        assertFalse(validator.isValid(file, null));
    }

    @Test
    void testIsValid_NullContentType_ReturnsFalse() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(null);

        assertFalse(validator.isValid(file, null));
    }
}
