package greencity.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = greencity.validator.ValidImageValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "Invalid image. Only JPG/PNG allowed and max size is 10MB.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
