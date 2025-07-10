package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "event_date_times")
@Builder
@ToString(exclude = "event")
@EqualsAndHashCode(exclude = "event")
public class EventDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean allDay;
}