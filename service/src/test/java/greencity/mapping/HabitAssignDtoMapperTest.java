package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HabitAssignDtoMapperTest {
    @InjectMocks
    HabitAssignDtoMapper mapper;

    @Test
    void convert_validHabitAssign_mapsCorrectly() {
        ZoneId zone = ZoneId.of("Europe/Kiev");
        ZonedDateTime createDate = ZonedDateTime.of(2025, 6, 27, 16, 44, 55, 617350400, zone);

        ZonedDateTime enrollZonedDate = ZonedDateTime.of(2025, 6, 26, 0, 0, 0, 0, zone);
        LocalDate enrollDate = enrollZonedDate.toLocalDate();

        ZonedDateTime lastEnrollmentDate = enrollZonedDate.minusDays(1);

        HabitStatusCalendar calendar = HabitStatusCalendar.builder()
                .id(101L)
                .enrollDate(enrollDate)
                .build();

        HabitAssign entity = HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.INPROGRESS)
                .createDate(createDate)
                .user(User.builder().id(77L).build())
                .duration(21)
                .habitStreak(3)
                .workingDays(5)
                .lastEnrollmentDate(lastEnrollmentDate)
                .habitStatusCalendars(List.of(calendar))
                .build();

        HabitAssignDto dto  = mapper.convert(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(HabitAssignStatus.INPROGRESS);
        assertThat(dto.getCreateDateTime()).isEqualTo(createDate);
        assertThat(dto.getLastEnrollmentDate()).isEqualTo(lastEnrollmentDate);
        assertThat(dto.getUserId()).isEqualTo(77L);
        assertThat(dto.getDuration()).isEqualTo(21);
        assertThat(dto.getHabitStreak()).isEqualTo(3);
        assertThat(dto.getWorkingDays()).isEqualTo(5);

        assertThat(dto.getHabitStatusCalendarDtoList()).hasSize(1);
        HabitStatusCalendarDto calendarDto =  dto.getHabitStatusCalendarDtoList().getFirst();
        assertThat(calendarDto.getId()).isEqualTo(101L);
        assertThat(calendarDto.getEnrollDate()).isEqualTo(enrollDate);
    }

    @Test
    void convert_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class,()->mapper.convert((HabitAssign) null));
    }

    @Test
    void convert_habitStatusCalendarIsNull_throwsNullPointerException() {
        HabitAssign entity = HabitAssign.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .habitStatusCalendars(null)
                .build();

        assertThrows(NullPointerException.class,()->mapper.convert(entity));
    }
}
