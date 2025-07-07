package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_subscriptions")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Builder
public class EmailSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(unique = true)
    @NotEmpty
    private String email;

    private ZonedDateTime lastSentEmailAt;
}
