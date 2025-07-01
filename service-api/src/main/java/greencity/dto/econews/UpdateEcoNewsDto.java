package greencity.dto.econews;

import greencity.constant.ServiceValidationConstants;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UpdateEcoNewsDto {
    @NotBlank
    private String id;

    @NotBlank
    @Size(min = 1, max = 170)
    private String title;

    @NotBlank
    @Size(min = 20, max = 63206)
    private String content;

    @NotEmpty(message = ServiceValidationConstants.MIN_AMOUNT_OF_TAGS)
    private List<String> tags;

    private String source;

    private String text;
}
