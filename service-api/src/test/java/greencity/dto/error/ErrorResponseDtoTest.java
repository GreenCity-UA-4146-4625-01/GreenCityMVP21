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
}