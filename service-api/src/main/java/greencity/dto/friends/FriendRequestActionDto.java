package greencity.dto.friends;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for sending or cancelling a friend request.
 *
 * Contains the ID of the target user to whom the action is applied.
 * @author Mykyta Sirobaba
 */

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class FriendRequestActionDto {
    @NotNull(message = "Target user ID must not be null")
    private Long targetUserId;
}
