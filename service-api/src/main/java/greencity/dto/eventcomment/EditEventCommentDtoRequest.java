package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditEventCommentDtoRequest {
    @NotBlank
    String text;

    private Set<Long> mentionedUserIds;
}
