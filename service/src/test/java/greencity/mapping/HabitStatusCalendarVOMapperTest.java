package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatusCalendar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HabitStatusCalendarVOMapperTest {

    @InjectMocks
    HabitStatusCalendarVOMapper habitStatusCalendarVOMapper;

    @Test
    @DisplayName("Should convert HabitStatusCalendar to HabitStatusCalendarVO")
    void shouldConvertHabitStatusCalendarToHabitStatusCalendarVO() {
        HabitStatusCalendar habitStatusCalendar = HabitStatusCalendar.builder()
                .id(1L)
                .enrollDate(LocalDate.of(2024, 6, 1))
                .habitAssign(HabitAssign.builder().id(100L).build())
                .build();

        HabitStatusCalendarVO habitStatusCalendarVOExpected = HabitStatusCalendarVO.builder()
                .id(1L)
                .enrollDate(habitStatusCalendar.getEnrollDate())
                .habitAssignVO(HabitAssignVO.builder().id(100L).build())
                .build();

        HabitStatusCalendarVO result = habitStatusCalendarVOMapper.convert(habitStatusCalendar);

        assertThat(result).usingRecursiveComparison().isEqualTo(habitStatusCalendarVOExpected);
    }
}