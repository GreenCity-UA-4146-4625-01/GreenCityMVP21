package greencity.validator;

import greencity.annotations.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    private static final long MAX_SIZE = 10485760; // 10 Mb

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return false;
        String contentType = file.getContentType();
        return (contentType != null &&
                (contentType.equals("image/jpeg") || contentType.equals("image/png")) &&
                file.getSize() <= MAX_SIZE);
    }
}
