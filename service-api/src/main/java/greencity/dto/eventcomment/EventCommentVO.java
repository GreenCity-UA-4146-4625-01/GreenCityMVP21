package greencity.dto.eventcomment;

import greencity.dto.user.UserVO;
import lombok.*;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCommentVO {
    @NotNull
    private Long id;

    @NotBlank
    @Size(min = 1, max = 8000)
    private String text;

    @NotNull
    private LocalDateTime createdDate;

    @NotNull
    private LocalDateTime modifiedDate;

    private boolean isOrganizerReply;

    private boolean deleted;

    private Long parentCommentId;

    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

    @Min(0)
    private int likes;

    @Min(0)
    private int replies;

    @NotNull
    private Set<UserVO> mentionedUserIds;

    @NotNull
    private Set<UserVO> usersLiked;
}