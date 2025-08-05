package greencity.dto.eventcomment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCommentEditViewDto {
    @NotNull
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String authorName;

    private String authorAvatar;

    @NotNull
    private LocalDateTime postedDate;

    @NotNull
    private LocalDateTime modifiedDate;

    @NotNull
    private Integer likesCount;

    @NotNull
    @Size(min = 1, max = 8000)
    private String text;
}
