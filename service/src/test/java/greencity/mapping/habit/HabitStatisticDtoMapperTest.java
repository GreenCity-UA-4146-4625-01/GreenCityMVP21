package greencity.mapping.habit;

import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.enums.HabitRate;
import greencity.mapping.HabitStatisticDtoMapper;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HabitStatisticDtoMapperTest {

    @Test
    void convertTest() {

        HabitAssign habitAssign = mock(HabitAssign.class);
        when(habitAssign.getId()).thenReturn(1L);

        HabitStatistic habitStatistic = mock(HabitStatistic.class);
        when(habitStatistic.getId()).thenReturn(1L);
        when(habitStatistic.getAmountOfItems()).thenReturn(2);
        when(habitStatistic.getCreateDate()).thenReturn(
                ZonedDateTime.of(
                        LocalDateTime.of(2025, 7, 2, 15, 30),
                        ZoneId.of("Europe/Berlin")
                )
        );
        when(habitStatistic.getHabitRate()).thenReturn(HabitRate.DEFAULT);
        when(habitStatistic.getHabitAssign()).thenReturn(habitAssign);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new HabitStatisticDtoMapper());

        HabitStatisticDto habitStatisticDto = modelMapper.map(habitStatistic, HabitStatisticDto.class);

        assertNotNull(habitStatisticDto);
        assertEquals(1L, habitStatisticDto.getId());
        assertEquals(habitStatisticDto.getAmountOfItems(), 2);
        assertEquals(habitStatisticDto.getCreateDate(), ZonedDateTime.of(LocalDateTime.of(2025, 7, 2, 15, 30),
                ZoneId.of("Europe/Berlin")));
        assertEquals(habitStatisticDto.getHabitRate(), HabitRate.DEFAULT);
        assertEquals(1L, habitStatisticDto.getHabitAssignId());
    }
}
