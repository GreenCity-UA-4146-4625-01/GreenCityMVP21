package greencity.mapping;

import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HabitAssignManagementDtoMapperTest {
    @InjectMocks
    private HabitAssignManagementDtoMapper mapper;

    @Test
    void convert_validEntity_mapsCorrectly() {
        ZonedDateTime now = ZonedDateTime.now();

        HabitAssign entity = HabitAssign.builder()
            .id(1L)
            .status(HabitAssignStatus.INPROGRESS)
            .createDate(now)
            .user(User.builder().id(100L).build())
            .habit(Habit.builder().id(50L).build())
            .duration(10)
            .habitStreak(2)
            .workingDays(5)
            .lastEnrollmentDate(now)
            .build();

        HabitAssignManagementDto dto = mapper.convert(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(HabitAssignStatus.INPROGRESS);
        assertThat(dto.getCreateDateTime()).isEqualTo(now);
        assertThat(dto.getUserId()).isEqualTo(100L);
        assertThat(dto.getHabitId()).isEqualTo(50L);
        assertThat(dto.getDuration()).isEqualTo(10);
        assertThat(dto.getHabitStreak()).isEqualTo(2);
        assertThat(dto.getWorkingDays()).isEqualTo(5);
        assertThat(dto.getLastEnrollment()).isEqualTo(now);
    }

    @Test
    void convert_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitAssign) null));
    }
}
