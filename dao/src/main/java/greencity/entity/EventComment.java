package greencity.entity;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "event_comment")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
public class EventComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 8000)
    private String text;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @ManyToOne
    private EventComment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventComment> replies = new ArrayList<>();

    @ManyToOne
    private User user;

    @ManyToOne
    private Event event;

    @Column(nullable = false)
    private boolean isOrganizerReply;

    @Column(nullable = false)
    private boolean deleted;

    @ManyToMany
    @JoinTable(
            name = "event_comment_users_liked",
            joinColumns = @JoinColumn(name = "event_comment_id"),
            inverseJoinColumns = @JoinColumn(name = "users_liked_id"))
    private Set<User> usersLiked;

    @ManyToMany
    @JoinTable(
            name = "event_comment_users_mentioned",
            joinColumns = @JoinColumn(name = "event_comment_id"),
            inverseJoinColumns = @JoinColumn(name = "mentioned_user_id"))
    private Set<User> mentionedUsers;
}
