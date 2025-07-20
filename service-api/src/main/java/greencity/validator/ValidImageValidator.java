package greencity.validator;

import greencity.annotations.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ValidImageValidator implements ConstraintValidator<ValidImage, List<MultipartFile>> {
    private static final long MAX_SIZE = 10485760; // 10 Mb

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) return true;

        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (contentType == null) {
                return false;
            }

            boolean isImage = contentType.equalsIgnoreCase("image/jpeg") ||
                    contentType.equalsIgnoreCase("image/png");

            if (!isImage) {
                return false;
            }

            if (file.getSize() > MAX_SIZE) {
                return false;
            }
        }
        return true;
    }
}
