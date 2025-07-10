package greencity.entity;

import greencity.enums.FriendRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a friend request between two users.
 *
 * This entity stores information about the sender and receiver of the friend request,
 * the creation timestamp, and the status of the request (e.g., pending, accepted, declined).
 *
 * Used to manage the lifecycle of friend requests before they are accepted and converted
 * into actual friendships.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

