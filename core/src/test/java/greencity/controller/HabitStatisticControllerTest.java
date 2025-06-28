package greencity.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.dto.habitstatistic.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitRate;
import greencity.service.HabitStatisticService;
import greencity.service.LanguageService;
import greencity.service.UserService;
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

import java.time.ZonedDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @BeforeEach
    void setup() {
        when(languageService.findAllLanguageCodes()).thenReturn(List.of("en", "ua"));
        UserVO mockUser = new UserVO();
        mockUser.setId(1L);
        when(userService.findByEmail(anyString())).thenReturn(mockUser);
    }

    @Test
    void testFindAllByHabitId() throws Exception {
        Long habitId = 1L;
        GetHabitStatisticDto dto = new GetHabitStatisticDto();
        when(habitStatisticService.findAllStatsByHabitId(habitId)).thenReturn(dto);

        mockMvc.perform(get("/habit/statistic/{habitId}", habitId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(habitStatisticService).findAllStatsByHabitId(habitId);
    }

    @Test
    void testFindAllStatsByHabitAssignId() throws Exception {
        Long habitAssignId = 2L;
        List<HabitStatisticDto> list = List.of(new HabitStatisticDto());
        when(habitStatisticService.findAllStatsByHabitAssignId(habitAssignId)).thenReturn(list);

        mockMvc.perform(get("/habit/statistic/assign/{habitAssignId}", habitAssignId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));

        verify(habitStatisticService).findAllStatsByHabitAssignId(habitAssignId);
    }

    @Test
    void testSaveHabitStatistic() throws Exception {
        Long habitId = TEST_HABIT_ID;
        Long userId = TEST_USER_ID;


        HabitStatisticDto responseDto = HabitStatisticDto.builder()
                .id(1L)
                .amountOfItems(TEST_AMOUNT)
                .habitRate(HabitRate.GOOD)
                .createDate(now)
                .build();


        AddHabitStatisticDto requestDto = new AddHabitStatisticDto();
        requestDto.setAmountOfItems(TEST_AMOUNT);;
        requestDto.setHabitRate(HabitRate.GOOD);
        requestDto.setCreateDate(now);



        when(habitStatisticService.saveByHabitIdAndUserId(
                eq(habitId),
                eq(userId),
                any(AddHabitStatisticDto.class)))
                .thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/habit/statistic/{habitId}", habitId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.amountOfItems").value(5))
                .andExpect(jsonPath("$.habitRate").value("GOOD"));

        verify(habitStatisticService).saveByHabitIdAndUserId(
                eq(habitId),
                eq(userId),
                any(AddHabitStatisticDto.class));
    }

    @Test
    void testUpdateStatistic() throws Exception {
        Long statisticId = 1L;
        Long userId = 1L;

        UpdateHabitStatisticDto responseDto = new UpdateHabitStatisticDto();
        responseDto.setAmountOfItems(10);
        responseDto.setHabitRate(HabitRate.BAD);

        UpdateHabitStatisticDto requestDto = new UpdateHabitStatisticDto();
        requestDto.setAmountOfItems(10);
        requestDto.setHabitRate(HabitRate.BAD);

        when(habitStatisticService.update(
                eq(statisticId),
                eq(userId),
                any(UpdateHabitStatisticDto.class)))
                .thenReturn(responseDto);

        String requestJson = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/habit/statistic/{id}", statisticId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountOfItems").value(10))
                .andExpect(jsonPath("$.habitRate").value("BAD"));


        verify(habitStatisticService).update(
                eq(statisticId),
                eq(userId),
                any(UpdateHabitStatisticDto.class));
    }

    @Test
    void testGetTodayStatisticsForAllHabitItems() throws Exception {
        String lang = "en";
        HabitItemsAmountStatisticDto responseDto = HabitItemsAmountStatisticDto.builder()
                .habitItem("Reusable cup")
                .notTakenItems(3L)
                .build();
        List<HabitItemsAmountStatisticDto> stats = List.of(responseDto);

        when(habitStatisticService.getTodayStatisticsForAllHabitItems(lang)).thenReturn(stats);

        mockMvc.perform(get("/habit/statistic/todayStatisticsForAllHabitItems")
                        .header(HttpHeaders.ACCEPT_LANGUAGE, lang))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(stats)));

        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(lang);
    }

    @Test
    void testFindAmountOfAcquiredHabits() throws Exception {
        Long userId = 7L;
        Long expected = 3L;
        when(habitStatisticService.getAmountOfAcquiredHabitsByUserId(userId)).thenReturn(expected);

        mockMvc.perform(get("/habit/statistic/acquired/count")
                        .param("userId", userId.toString()))
                    .andExpect(status().isOk())
                .andExpect(content().string(expected.toString()));

        verify(habitStatisticService).getAmountOfAcquiredHabitsByUserId(userId);
    }

    @Test
    void testFindAmountOfHabitsInProgress() throws Exception {
        Long userId = 8L;
        Long expected = 5L;
        when(habitStatisticService.getAmountOfHabitsInProgressByUserId(userId)).thenReturn(expected);

        mockMvc.perform(get("/habit/statistic/in-progress/count")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expected.toString()));

        verify(habitStatisticService).getAmountOfHabitsInProgressByUserId(userId);
    }
}

