package greencity.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.dto.habitstatistic.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
import greencity.service.LanguageService;
import greencity.service.UserService;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HabitStatisticController.class)
@ContextConfiguration(classes = GreenCityApplication.class)
@WithMockUser(username = "Admin", roles = {"ADMIN"})
class HabitStatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HabitStatisticService habitStatisticService;

    @MockBean
    private UserService userService;

    @MockBean
    private LanguageService languageService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private final ZonedDateTime now = ZonedDateTime.now();
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_HABIT_ID = 1L;
    private static final Integer TEST_AMOUNT = 5;
    private static final Long TEST_STATISTIC_ID = 1L;
    private static final String TEST_LANGUAGE = "en";

    @BeforeEach
    void setup() {
        when(languageService.findAllLanguageCodes()).thenReturn(
                List.of("en", "ua")
        );
        UserVO mockUser = new UserVO();
        mockUser.setId(1L);
        when(userService.findByEmail(anyString())).thenReturn(mockUser);
    }

    @Test
    void testFindAllByHabitId() throws Exception {
        GetHabitStatisticDto dto = new GetHabitStatisticDto();
        when(habitStatisticService.findAllStatsByHabitId(TEST_HABIT_ID)).thenReturn(
                dto
        );

        mockMvc
                .perform(get("/habit/statistic/{habitId}", TEST_HABIT_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(habitStatisticService).findAllStatsByHabitId(TEST_HABIT_ID);
    }

    @Test
    void testFindAllStatsByHabitAssignId() throws Exception {
        List<HabitStatisticDto> list = List.of(new HabitStatisticDto());
        when(
                habitStatisticService.findAllStatsByHabitAssignId(TEST_HABIT_ID)
        ).thenReturn(list);

        mockMvc
                .perform(get("/habit/statistic/assign/{habitAssignId}", TEST_HABIT_ID))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));

        verify(habitStatisticService).findAllStatsByHabitAssignId(TEST_HABIT_ID);
    }

    @Test
    void testSaveHabitStatistic() throws Exception {
        HabitStatisticDto responseDto = HabitStatisticDto.builder()
                .id(TEST_STATISTIC_ID)
                .amountOfItems(TEST_AMOUNT)
                .habitRate(HabitRate.GOOD)
                .createDate(now)
                .build();

        AddHabitStatisticDto requestDto = new AddHabitStatisticDto();
        requestDto.setAmountOfItems(TEST_AMOUNT);
        requestDto.setHabitRate(HabitRate.GOOD);
        requestDto.setCreateDate(now);

        when(
                habitStatisticService.saveByHabitIdAndUserId(
                        eq(TEST_HABIT_ID),
                        eq(TEST_USER_ID),
                        any(AddHabitStatisticDto.class)
                )
        ).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc
                .perform(
                        post("/habit/statistic/{habitId}", TEST_HABIT_ID)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_STATISTIC_ID))
                .andExpect(jsonPath("$.amountOfItems").value(TEST_AMOUNT))
                .andExpect(jsonPath("$.habitRate").value("GOOD"));

        verify(habitStatisticService).saveByHabitIdAndUserId(
                eq(TEST_HABIT_ID),
                eq(TEST_USER_ID),
                any(AddHabitStatisticDto.class)
        );
    }

    @Test
    void testUpdateStatistic() throws Exception {
        UpdateHabitStatisticDto responseDto = new UpdateHabitStatisticDto();
        responseDto.setAmountOfItems(TEST_AMOUNT);
        responseDto.setHabitRate(HabitRate.BAD);

        UpdateHabitStatisticDto requestDto = new UpdateHabitStatisticDto();
        requestDto.setAmountOfItems(TEST_AMOUNT);
        requestDto.setHabitRate(HabitRate.BAD);

        when(
                habitStatisticService.update(
                        eq(TEST_STATISTIC_ID),
                        eq(TEST_USER_ID),
                        any(UpdateHabitStatisticDto.class)
                )
        ).thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc
                .perform(
                        put("/habit/statistic/{id}", TEST_STATISTIC_ID)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountOfItems").value(TEST_AMOUNT))
                .andExpect(jsonPath("$.habitRate").value("BAD"));

        verify(habitStatisticService).update(
                eq(TEST_STATISTIC_ID),
                eq(TEST_USER_ID),
                any(UpdateHabitStatisticDto.class)
        );
    }

    @Test
    void testGetTodayStatisticsForAllHabitItems() throws Exception {
        List<HabitItemsAmountStatisticDto> stats = List.of(
                new HabitItemsAmountStatisticDto()
        );

        when(
                habitStatisticService.getTodayStatisticsForAllHabitItems(TEST_LANGUAGE)
        ).thenReturn(stats);

        mockMvc
                .perform(
                        get("/habit/statistic/todayStatisticsForAllHabitItems").header(
                                HttpHeaders.ACCEPT_LANGUAGE,
                                TEST_LANGUAGE
                        )
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stats)));

        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(
                TEST_LANGUAGE
        );
    }

    @Test
    void testFindAmountOfAcquiredHabits() throws Exception {
        Long userId = 7L;
        Long expected = 3L;
        when(
                habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId)
        ).thenReturn(expected);

        mockMvc
                .perform(
                        get("/habit/statistic/acquired/count").param("userId", userId.toString())
                )
                .andExpect(status().isOk())
                .andExpect(content().string(expected.toString()));

        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(userId);
    }

    @Test
    void testFindAmountOfHabitsInProgress() throws Exception {
        Long userId = 8L;
        Long expected = 5L;
        when(
                habitStatisticService.getAmountOfHabitsInProgressByUserId(userId)
        ).thenReturn(expected);

        mockMvc
                .perform(
                        get("/habit/statistic/in-progress/count").param(
                                "userId",
                                userId.toString()
                        )
                )
                .andExpect(status().isOk())
                .andExpect(content().string(expected.toString()));

        verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(userId);
    }
}