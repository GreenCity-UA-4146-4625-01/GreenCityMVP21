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

class HabitStatisticDtoMapperTest {

    @Test
    void convertTest() {

        HabitAssign habitAssign = HabitAssign.builder()
                .id(1L)
                .build();

        HabitStatistic habitStatistic = HabitStatistic.builder()
                .id(1L)
                .amountOfItems(2)
                .createDate(ZonedDateTime.of(
                        LocalDateTime.of(2025, 7, 2, 15, 30),
                        ZoneId.of("Europe/Berlin")
                ))
                .habitRate(HabitRate.DEFAULT)
                .habitAssign(habitAssign)
                .build();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new HabitStatisticDtoMapper());

        HabitStatisticDto habitStatisticDto = modelMapper.map(habitStatistic, HabitStatisticDto.class);

        assertNotNull(habitStatisticDto);
        assertEquals(1L, habitStatisticDto.getId());
        assertEquals(2, habitStatisticDto.getAmountOfItems());
        assertEquals(habitStatisticDto.getCreateDate(), ZonedDateTime.of(LocalDateTime.of(2025, 7, 2, 15, 30),
                ZoneId.of("Europe/Berlin")));
        assertEquals(HabitRate.DEFAULT, habitStatisticDto.getHabitRate());
        assertEquals(1L, habitStatisticDto.getHabitAssignId());
    }
}
