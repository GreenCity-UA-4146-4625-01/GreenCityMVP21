package greencity.dto.eventcomment;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AddEventCommentDtoRequest {
    @NotBlank(message = "The text of comment can not be empty")
    @Length(min = 1, max = 8000)
    private String text;

    private Long parentCommentId;

    private Set<Long> mentionedUserIds = new HashSet<>();
}
