package greencity.mapping.habit;

import greencity.dto.habit.HabitAssignUserDurationDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.enums.HabitAssignStatus;
import greencity.mapping.HabitAssignUserDurationDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HabitAssignUserDurationDtoMapperTest {

    @Test
    void convertMethodTest() {

        HabitAssign habitAssign = HabitAssign.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .habit(Habit.builder().id(1L).build())
                .status(HabitAssignStatus.ACTIVE)
                .workingDays(1)
                .duration(2)
                .build();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new HabitAssignUserDurationDtoMapper());

        HabitAssignUserDurationDto dto = modelMapper.map(habitAssign, HabitAssignUserDurationDto.class);

        assertEquals(1L, dto.getHabitAssignId());
        assertEquals(1L, dto.getUserId());
        assertEquals(1L, dto.getHabitId());
        assertEquals(HabitAssignStatus.ACTIVE, dto.getStatus());
        assertEquals(1, dto.getWorkingDays());
        assertEquals(2, dto.getDuration());
    }
}
