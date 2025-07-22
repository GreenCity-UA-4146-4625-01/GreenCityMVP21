package greencity.dto.eventcomment;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCommentDtoResponse {
    @NotNull
    @Min(1)
    private Long id;

    @NotNull
    private EventCommentAuthorDto author;

    @NotBlank
    private String text;

    @NotNull
    private LocalDateTime modifiedDate;

    private Set<EventShortInfoUserVO> mentionedUser;
}

