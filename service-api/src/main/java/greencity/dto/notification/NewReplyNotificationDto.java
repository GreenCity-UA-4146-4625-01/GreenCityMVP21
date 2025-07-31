package greencity.dto.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.user.UserVO;
import greencity.util.NotificationTextFormatter;
import lombok.Builder;

@Builder
@JsonTypeName("NEW_REPLY")
public record NewReplyNotificationDto(
    boolean isRead,
    UserVO receiver,
    EcoNewsCommentDto comment,
    EcoNewsCommentDto parentComment
) implements NotificationDto {
    @Override
    public long objectId() {
        return comment.getId();
    }

    @Override
    public String text() {
        return NotificationTextFormatter.formatNewReplyText(
                comment.getAuthor().getName(),
                "news",
                comment.getEcoNewsTitle(),
                comment.getModifiedDate()
        );
    }

}
