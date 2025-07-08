package greencity.mapping;

import greencity.service.MultipartFileImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MultipartBase64ImageMapperTest {

    @InjectMocks
    private MultipartBase64ImageMapper mapper;
    private String validBase64Image;
    private File tempFile;

    @BeforeEach
    void setUp() {
        validBase64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";
        tempFile = new File("tempImage.jpg");
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
            System.err.println("Failed to delete temporary file.");
        }
    }

    @Test
    void convert_ValidBase64Image_ShouldReturnMultipartFile() {
        String base64Image = validBase64Image;

        MultipartFile result = mapper.convert(base64Image);

        assertNotNull(result);
        assertInstanceOf(MultipartFileImpl.class, result);
        assertEquals("mainFile", result.getName());
        assertEquals("tempImage.jpg", result.getOriginalFilename());
        assertTrue(result.getSize() > 0);

        try {
            assertNotNull(result.getBytes());
            assertTrue(result.getBytes().length > 0);
        } catch (IOException e) {
            fail("Failed to get bytes from MultipartFile: " + e.getMessage());
        }
    }

    @Test
    void convert_NullInput_ShouldThrowException() {
        String base64Image = null;

        assertThrows(Exception.class, () -> mapper.convert(base64Image));
    }

    @Test
    void convert_EmptyString_ShouldThrowException() {
        String base64Image = "";

        assertThrows(Exception.class, () -> mapper.convert(base64Image));
    }

    @Test
    void convert_Base64WithoutDataPrefix_ShouldThrowException() {
        String base64Image = Base64.getEncoder().encodeToString("test".getBytes());

        assertThrows(Exception.class, () -> mapper.convert(base64Image));
    }

    @Test
    void convert_ValidJpegBase64Image_ShouldReturnMultipartFile() {
        String jpegBase64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A8A";

        MultipartFile result = mapper.convert(jpegBase64);

        assertNotNull(result);
        assertEquals("mainFile", result.getName());
        assertEquals("tempImage.jpg", result.getOriginalFilename());
        assertTrue(result.getSize() > 0);
    }


    @Test
    void convert_ValidImage_ShouldCreateTempFile() {
        String base64Image = validBase64Image;

        MultipartFile result = mapper.convert(base64Image);

        assertNotNull(result);
        assertTrue(result.getSize() > 0);
        assertEquals("mainFile", result.getName());
    }
}