package greencity.mapping;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.user.UserShoppingListItemAdvanceDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.UserShoppingListItem;
import greencity.enums.HabitAssignStatus;
import greencity.enums.ShoppingListItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HabitAssignMapperTest {
    @InjectMocks
    private HabitAssignMapper mapper;

    ZonedDateTime nowZoned = ZonedDateTime.now();
    LocalDateTime nowLocal = LocalDateTime.now();

    @Test
    void convert_validDto_mapsCorrectly() {

        UserShoppingListItem inProgressItem = UserShoppingListItem.builder()
            .id(1L)
            .status(ShoppingListItemStatus.INPROGRESS)
            .dateCompleted(nowLocal)
            .build();

        UserShoppingListItem doneItem = UserShoppingListItem.builder()
            .id(2L)
            .status(ShoppingListItemStatus.DONE)
            .dateCompleted(nowLocal)
            .build();

        HabitAssign dto = HabitAssign.builder()
            .id(1L)
            .duration(10)
            .habitStreak(3)
            .createDate(nowZoned)
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(5)
            .lastEnrollmentDate(nowZoned)
            .habit(Habit.builder().id(99L).complexity(2).build())
            .userShoppingListItems(List.of(inProgressItem, doneItem))
            .build();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(mapper);
        HabitAssignDto entity = modelMapper.map(dto, HabitAssignDto.class);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getDuration()).isEqualTo(10);
        assertThat(entity.getHabitStreak()).isEqualTo(3);
        assertThat(entity.getCreateDateTime()).isEqualTo(nowZoned);
        assertThat(entity.getStatus()).isEqualTo(HabitAssignStatus.INPROGRESS);
        assertThat(entity.getWorkingDays()).isEqualTo(5);
        assertThat(entity.getLastEnrollmentDate()).isEqualTo(nowZoned);
        assertThat(entity.getHabit().getId()).isEqualTo(99L);
        assertThat(entity.getUserShoppingListItems()).hasSize(2);
        assertThat(entity.getUserShoppingListItems().getFirst().getId()).isEqualTo(1L);
        assertThat(entity.getUserShoppingListItems().getFirst().getStatus())
            .isEqualTo(ShoppingListItemStatus.INPROGRESS);
        assertThat(entity.getUserShoppingListItems().get(1).getId()).isEqualTo(2L);
        assertThat(entity.getUserShoppingListItems().get(1).getStatus()).isEqualTo(ShoppingListItemStatus.DONE);
    }

    @Test
    void convert_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> mapper.convert((HabitAssignDto) null));
    }

    @Test
    void convert_emptyShoppingListItems_mapsToEmptyList() {
        HabitAssignDto dto = HabitAssignDto.builder()
            .id(1L)
            .duration(10)
            .habitStreak(2)
            .createDateTime(nowZoned)
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(3)
            .lastEnrollmentDate(nowZoned)
            .habit(HabitDto.builder().id(100L).complexity(1).build())
            .userShoppingListItems(Collections.emptyList())
            .build();
        HabitAssign entity = mapper.convert(dto);

        assertThat(entity.getUserShoppingListItems()).isEmpty();
    }

    @Test
    void convert_nullShoppingListItems_throwsNullPointerException() {
        HabitAssignDto dto = HabitAssignDto.builder()
            .id(1L)
            .habit(HabitDto.builder().id(1L).complexity(1).build())
            .userShoppingListItems(null)
            .build();

        assertThatThrownBy(() -> mapper.convert(dto))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void convert_emptyDto_fieldsAreNullOrDefaults() {
        HabitAssignDto dto = HabitAssignDto.builder()
            .habit(HabitDto.builder().id(1L).complexity(1).build())
            .userShoppingListItems(Collections.emptyList())
            .build();

        HabitAssign entity = mapper.convert(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getDuration()).isNull();
        assertThat(entity.getHabitStreak()).isNull();
    }

    @Test
    void convert_onlyDoneItems_resultsInEmptyShoppingList() {
        UserShoppingListItemAdvanceDto doneItem = UserShoppingListItemAdvanceDto.builder()
            .id(1L)
            .status(ShoppingListItemStatus.DONE)
            .dateCompleted(nowLocal)
            .build();

        HabitAssignDto dto = HabitAssignDto.builder()
            .id(100L)
            .duration(30)
            .habitStreak(5)
            .createDateTime(nowZoned)
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(7)
            .lastEnrollmentDate(nowZoned)
            .habit(HabitDto.builder().id(10L).complexity(1).build())
            .userShoppingListItems(List.of(doneItem))
            .build();

        HabitAssign entity = mapper.convert(dto);

        assertThat(entity.getUserShoppingListItems()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideStatusItems")
    void convert_variousStatuses_filtersCorrectly(List<UserShoppingListItemAdvanceDto> inputItems, int expectedSize) {
        HabitAssignDto dto = HabitAssignDto.builder()
            .id(4L)
            .duration(10)
            .habitStreak(2)
            .createDateTime(nowZoned)
            .status(HabitAssignStatus.INPROGRESS)
            .workingDays(3)
            .lastEnrollmentDate(nowZoned)
            .habit(HabitDto.builder().id(100L).complexity(1).build())
            .userShoppingListItems(inputItems)
            .build();

        HabitAssign entity = mapper.convert(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getUserShoppingListItems()).hasSize(expectedSize);

        assertThat(entity.getUserShoppingListItems())
            .allMatch(item -> item.getStatus() == ShoppingListItemStatus.INPROGRESS);
    }

    static Stream<Arguments> provideStatusItems() {
        LocalDateTime now = LocalDateTime.now();

        UserShoppingListItemAdvanceDto inProgress1 = UserShoppingListItemAdvanceDto.builder()
            .id(1L)
            .status(ShoppingListItemStatus.INPROGRESS)
            .dateCompleted(now)
            .shoppingListItemId(10L)
            .build();

        UserShoppingListItemAdvanceDto done1 = UserShoppingListItemAdvanceDto.builder()
            .id(2L)
            .status(ShoppingListItemStatus.DONE)
            .dateCompleted(now)
            .shoppingListItemId(11L)
            .build();

        UserShoppingListItemAdvanceDto inProgress2 = UserShoppingListItemAdvanceDto.builder()
            .id(3L)
            .status(ShoppingListItemStatus.INPROGRESS)
            .dateCompleted(now)
            .shoppingListItemId(12L)
            .build();

        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(List.of(inProgress1, done1), 1),
            org.junit.jupiter.params.provider.Arguments.of(List.of(done1), 0),
            org.junit.jupiter.params.provider.Arguments.of(List.of(inProgress1, inProgress2), 2),
            org.junit.jupiter.params.provider.Arguments.of(List.of(), 0));
    }
}
