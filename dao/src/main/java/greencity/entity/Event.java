package greencity.entity;

import greencity.enums.EventType;
import greencity.enums.EventVisibility;
import jakarta.persistence.*;
import lombok.*;

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

    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventVisibility eventVisibility;

    @ElementCollection(targetClass = EventType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "event_types", joinColumns = @JoinColumn(name = "event_id"))
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

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventDateTime> eventDateTimes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

}