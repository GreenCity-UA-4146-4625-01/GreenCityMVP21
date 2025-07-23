package greencity.entity;

import greencity.enums.EventType;
import greencity.enums.EventVisibility;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "events")
@Builder
@ToString(exclude = {"eventLocations", "eventImages", "eventDateTimes", "creator"})
@EqualsAndHashCode(exclude = {"eventLocations", "eventImages", "eventDateTimes", "creator"})
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 70, nullable = false)
    private String title;

    @Column(length = 63206, nullable = false)
    private String description;

    private Long mainImageId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdateDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventVisibility eventVisibility;

    @ElementCollection(targetClass = EventType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "event_event_types", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "event_type")
    private Set<EventType> eventTypes = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventLocation> eventLocations;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "event_online_links", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "online_link")
    private List<String> onlineLinks;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventImage> eventImages;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventDateTime> eventDateTimes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;
}
