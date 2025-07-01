package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.entity.Habit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomHabitMapperTest {
    @InjectMocks
    private CustomHabitMapper customHabitMapper;

    @Test
    void convertTestSuccess() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = new AddCustomHabitDtoRequest();
        addCustomHabitDtoRequest.setImage("example image");
        addCustomHabitDtoRequest.setComplexity(1);
        addCustomHabitDtoRequest.setDefaultDuration(1);

        Habit expectedHabit = Habit.builder()
            .image(addCustomHabitDtoRequest.getImage())
            .complexity(addCustomHabitDtoRequest.getComplexity())
            .defaultDuration(addCustomHabitDtoRequest.getDefaultDuration())
            .isCustomHabit(true)
            .build();

        assertEquals(expectedHabit, customHabitMapper.convert(addCustomHabitDtoRequest));
    }

    @Test
    void convertTestWithNullFields() {
        AddCustomHabitDtoRequest addCustomHabitDtoRequest = new AddCustomHabitDtoRequest();

        Habit expectedHabit = Habit.builder()
            .isCustomHabit(true)
            .build();

        assertEquals(expectedHabit, customHabitMapper.convert(addCustomHabitDtoRequest));
        assertNull(addCustomHabitDtoRequest.getImage());
        assertNull(addCustomHabitDtoRequest.getComplexity());
        assertNull(addCustomHabitDtoRequest.getDefaultDuration());
    }
}
