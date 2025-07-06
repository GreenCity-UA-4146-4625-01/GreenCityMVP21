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
class HabitStatusCalendarMapperTest {

    @InjectMocks
    HabitStatusCalendarMapper habitStatusCalendarMapper;

    @Test
    @DisplayName("Should convert HabitStatusCalendarVO to HabitStatusCalendar.")
    void shouldConvertHabitStatusCalendarVOToHabitStatusCalendar() {
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
                .id(1L)
                .enrollDate(LocalDate.of(2024, 6, 1))
                .habitAssignVO(HabitAssignVO.builder().id(100L).build())
                .build();

        HabitStatusCalendar habitStatusCalendarExpected = HabitStatusCalendar.builder()
                .id(1L)
                .enrollDate(habitStatusCalendarVO.getEnrollDate())
                .habitAssign(HabitAssign.builder().id(100L).build())
                .build();

        HabitStatusCalendar result = habitStatusCalendarMapper.convert(habitStatusCalendarVO);

        assertThat(result).usingRecursiveComparison().isEqualTo(habitStatusCalendarExpected);
    }
}