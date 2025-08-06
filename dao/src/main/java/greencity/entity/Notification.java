package greencity.entity;

import greencity.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private NotificationType type;

    private long objectId;

    private boolean isRead;

    @ManyToOne
    private User receiver;

    @Column(name = "text")
    private String text;

    private ZonedDateTime creationDate;

    private String content;

    @ElementCollection
    @CollectionTable(name = "notification_commentators", joinColumns = @JoinColumn(name = "notification_id"))
    private List<String> commentators = new ArrayList<>();
}
