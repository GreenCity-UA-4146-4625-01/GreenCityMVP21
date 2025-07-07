package greencity.dto.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseDtoTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .error("OK")
                .message("Everything is fine")
                .path("/api/test")
                .build();

        Set<ConstraintViolation<ErrorResponseDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(),
                () -> "Expected no violations, but got: " + violations);
    }

    @Test
    void whenTimestampNull_thenNotNullViolation() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .timestamp(null)
                .status(200)
                .error("OK")
                .message("msg")
                .path("/p")
                .build();

        Set<ConstraintViolation<ErrorResponseDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<ErrorResponseDto> v = violations.iterator().next();
        assertEquals("timestamp", v.getPropertyPath().toString());
        assertEquals("must not be null", v.getMessage());
    }

    @Test
    void whenStatusNonPositive_thenPositiveViolation() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(0)
                .error("OK")
                .message("msg")
                .path("/p")
                .build();

        Set<ConstraintViolation<ErrorResponseDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<ErrorResponseDto> v = violations.iterator().next();
        assertEquals("status", v.getPropertyPath().toString());
        assertEquals("must be greater than 0", v.getMessage());
    }

    @Test
    void whenErrorBlank_thenNotBlankViolation() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .error("   ")
                .message("msg")
                .path("/p")
                .build();

        Set<ConstraintViolation<ErrorResponseDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<ErrorResponseDto> v = violations.iterator().next();
        assertEquals("error", v.getPropertyPath().toString());
        assertEquals("must not be blank", v.getMessage());
    }

    @Test
    void whenMessageBlank_thenNotBlankViolation() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .error("Err")
                .message("")
                .path("/p")
                .build();

        Set<ConstraintViolation<ErrorResponseDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<ErrorResponseDto> v = violations.iterator().next();
        assertEquals("message", v.getPropertyPath().toString());
        assertEquals("must not be blank", v.getMessage());
    }

    @Test
    void whenPathBlank_thenNotBlankViolation() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .error("Err")
                .message("msg")
                .path(" ")
                .build();

        Set<ConstraintViolation<ErrorResponseDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
        ConstraintViolation<ErrorResponseDto> v = violations.iterator().next();
        assertEquals("path", v.getPropertyPath().toString());
        assertEquals("must not be blank", v.getMessage());
    }

    @Test
    void noArgsConstructor_DefaultValuesAreNullOrZero() {
        ErrorResponseDto dto = new ErrorResponseDto();

        assertNull(dto.getTimestamp(), "timestamp should be null by default");
        assertEquals(0, dto.getStatus(), "status should default to 0");
        assertNull(dto.getError(), "error should be null by default");
        assertNull(dto.getMessage(), "message should be null by default");
        assertNull(dto.getPath(), "path should be null by default");
    }

    @Test
    void allArgsConstructor_FieldsAreReturnedByGetters() {
        LocalDateTime now = LocalDateTime.of(2025, 7, 7, 12, 0);
        int status = 418;
        String error   = "I'm a teapot";
        String message = "Short and stout";
        String path    = "/tea";

        ErrorResponseDto dto = new ErrorResponseDto(now, status, error, message, path);

        assertEquals(now,      dto.getTimestamp());
        assertEquals(status,   dto.getStatus());
        assertEquals(error,    dto.getError());
        assertEquals(message,  dto.getMessage());
        assertEquals(path,     dto.getPath());
    }

    @Test
    void builder_ConstructsObjectEquivalentlyToAllArgs() {
        LocalDateTime now = LocalDateTime.of(2025, 7, 7, 13, 30);
        int status = 202;
        String error   = "Accepted";
        String message = "Request accepted";
        String path    = "/accept";

        ErrorResponseDto dto = ErrorResponseDto.builder()
                .timestamp(now)
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();

        assertEquals(now,      dto.getTimestamp());
        assertEquals(status,   dto.getStatus());
        assertEquals(error,    dto.getError());
        assertEquals(message,  dto.getMessage());
        assertEquals(path,     dto.getPath());
    }
}