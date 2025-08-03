package greencity.entity;

import greencity.enums.FriendStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_friends")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserFriend {

    @EmbeddedId
    private UserFriendId userFriendId;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("friend")
    @JoinColumn(name = "friend_id")
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();
}

