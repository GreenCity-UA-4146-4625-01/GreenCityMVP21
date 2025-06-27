package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.HabitAssignService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitAssignControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private HabitAssignController habitAssignController;

    @Mock
    private HabitAssignService habitAssignService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator validator;

    private final String link = "/habit/assign";
    private final String testEmail = "tom@gmail.com";
    private final Locale locale = Locale.ENGLISH;
    private final LocalDate date = LocalDate.now();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setValidator(validator)
                .build();
    }


    @Test
    void assignDefault() throws Exception {
        Long habitId = 1L;
        UserVO userVO = prepareMockUser();

        HabitAssignManagementDto habitAssignManagementDto = new HabitAssignManagementDto();
        habitAssignManagementDto.setHabitId(habitId);

        when(habitAssignService.assignDefaultHabitForUser(habitId, userVO))
                .thenReturn(habitAssignManagementDto);

        mockMvc.perform(post(link + "/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.habitId").value(habitId));

        verify(habitAssignService).assignDefaultHabitForUser(eq(habitId), any());
    }

    @Test
    void assignCustom() throws Exception {
        String requestBody = """
                {
                  "habitAssignPropertiesDto": {
                    "duration": 20,
                    "defaultShoppingListItems": [1, 2, 3]
                  },
                  "friendsIdsList": [2, 3, 4]
                }
                """;
        Long habitId = 1L;

        HabitAssignManagementDto habitAssignManagementDto = HabitAssignManagementDto.builder().id(habitId).build();

        when(habitAssignService.assignCustomHabitForUser(eq(habitId), any(), any()))
                .thenReturn(Lists.newArrayList(habitAssignManagementDto));

        mockMvc.perform(post(link + "/{habitId}/custom", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(habitAssignService).assignCustomHabitForUser(eq(habitId), any(), any());
    }

    @Test
    void updateHabitAssignDuration() throws Exception {
        Long habitAssignId = 1L;
        UserVO userVO = prepareMockUser();

        HabitAssignUserDurationDto habitAssignUserDurationDto = HabitAssignUserDurationDto.builder()
                .habitAssignId(habitAssignId)
                .userId(userVO.getId())
                .habitId(habitAssignId)
                .duration(10)
                .workingDays(5)
                .status(HabitAssignStatus.REQUESTED)
                .build();

        when(habitAssignService.updateUserHabitInfoDuration(1L, 1L, 10))
                .thenReturn(habitAssignUserDurationDto);

        mockMvc.perform(put(link + "/{habitAssignId}/update-habit-duration", habitAssignId)
                        .param("duration", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration").value(10));

        verify(habitAssignService).updateUserHabitInfoDuration(1L, 1L, 10);
    }

    @Test
    void getHabitAssign() throws Exception {
        Long habitAssignId = 1L;
        UserVO userVO = prepareMockUser();

        HabitAssignDto habitAssignDto = new HabitAssignDto();
        habitAssignDto.setId(habitAssignId);

        when(habitAssignService.getByHabitAssignIdAndUserId(habitAssignId, userVO.getId(), locale.getLanguage()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(get(link + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).getByHabitAssignIdAndUserId(1L, 1L, locale.getLanguage());
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquired() throws Exception {
        UserVO userVO = prepareMockUser();

        when(habitAssignService.getAllHabitAssignsByUserIdAndStatusNotCancelled(userVO.getId(), locale.getLanguage()))
                .thenReturn(List.of());

        mockMvc.perform(get(link + "/allForCurrentUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(userVO.getId(), locale.getLanguage());
    }

    @Test
    void getUserShoppingAndCustomShoppingLists() throws Exception {
        Long habitAssignId = 1L;
        UserVO userVO = prepareMockUser();

        UserShoppingAndCustomShoppingListsDto userShoppingAndCustomShoppingListsDto = new UserShoppingAndCustomShoppingListsDto();

        when(habitAssignService.getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, locale.getLanguage()))
                .thenReturn(userShoppingAndCustomShoppingListsDto);

        mockMvc.perform(get(link + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(userVO.getId(), habitAssignId, locale.getLanguage());
    }

    @Test
    void updateUserAndCustomShoppingLists() throws Exception {
        Long habitAssignId = 1L;
        UserVO userVO = prepareMockUser();

        UserShoppingAndCustomShoppingListsDto listsDto = UserShoppingAndCustomShoppingListsDto.builder()
                .userShoppingListItemDto(List.of())
                .customShoppingListItemDto(List.of())
                .build();
        System.out.println(listsDto.toString());
        System.out.println(objectMapper.writeValueAsString(listsDto));

        doNothing().when(habitAssignService)
                .fullUpdateUserAndCustomShoppingLists(
                        userVO.getId(), habitAssignId, listsDto, locale.getLanguage());

        mockMvc.perform(put(link + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
                        .principal(() -> testEmail)
                        .content(objectMapper.writeValueAsString(listsDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService)
                .fullUpdateUserAndCustomShoppingLists(
                        userVO.getId(), habitAssignId, listsDto, locale.getLanguage());
    }

    @Test
    void getListOfUserAndCustomShoppingListsInprogress() throws Exception {
        UserVO userVO = prepareMockUser();

        UserShoppingAndCustomShoppingListsDto userShoppingAndCustomShoppingListsDto = UserShoppingAndCustomShoppingListsDto.builder()
                .userShoppingListItemDto(List.of()).customShoppingListItemDto(List.of()).build();

        when(habitAssignService
                .getListOfUserAndCustomShoppingListsWithStatusInprogress(userVO.getId(), locale.getLanguage())).thenReturn(
                List.of(userShoppingAndCustomShoppingListsDto)
        );

        mockMvc.perform(get(link + "/allUserAndCustomShoppingListsInprogress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userShoppingAndCustomShoppingListsDto))
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(userVO.getId(), locale.getLanguage());
    }

    @Test
    void getAllHabitAssignsByHabitIdAndAcquired() throws Exception {
        Long habitId = 1L;
        when(habitAssignService.getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, locale.getLanguage()))
                .thenReturn(List.of());

        mockMvc.perform(get(link + "/{habitId}/all", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(habitId, locale.getLanguage());
    }

    @Test
    void getHabitAssignByHabitId() throws Exception {
        Long habitId = 1L;
        UserVO userVO = prepareMockUser();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(habitId).build();

        when(habitAssignService.findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, locale.getLanguage()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(get(link + "/{habitId}/active", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(userVO.getId(), habitId, locale.getLanguage());
    }

    @Test
    void getUsersHabitByHabitAssignId() throws Exception {
        Long habitAssignId = 1L;
        Long habitId = 1L;
        UserVO userVO = prepareMockUser();

        HabitDto habitDto = HabitDto.builder().id(habitId).build();

        when(habitAssignService.findHabitByUserIdAndHabitAssignId(userVO.getId(), habitAssignId, locale.getLanguage()))
                .thenReturn(habitDto);

        mockMvc.perform(get(link + "/{habitAssignId}/more", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(userVO.getId(), habitId, locale.getLanguage());
    }

    @Test
    void updateAssignByHabitId() throws Exception {
        Long habitAssignId = 1L;
        Long habitId = 1L;

        HabitAssignStatDto habitAssignStatDto = HabitAssignStatDto.builder().status(HabitAssignStatus.INPROGRESS).build();
        HabitAssignManagementDto habitAssignManagementDto = HabitAssignManagementDto.builder().habitId(habitId).build();

        when(habitAssignService.updateStatusByHabitAssignId(habitAssignId, habitAssignStatDto))
                .thenReturn(habitAssignManagementDto);

        mockMvc.perform(patch(link + "/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitAssignStatDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateStatusByHabitAssignId(habitAssignId, habitAssignStatDto);
    }

    @Test
    void enrollHabit() throws Exception {
        Long habitAssignId = 1L;

        UserVO userVO = prepareMockUser();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(habitAssignId).build();

        when(habitAssignService.enrollHabit(habitAssignId, userVO.getId(), date, locale.getLanguage()))
                .thenReturn(habitAssignDto);

        mockMvc.perform(post(link + "/{habitAssignId}/enroll/{date}", habitAssignId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).enrollHabit(habitAssignId, userVO.getId(), date, locale.getLanguage());
    }

    @Test
    void unenrollHabit() throws Exception {
        Long habitAssignId = 1L;

        UserVO userVO = prepareMockUser();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(habitAssignId).build();

        when(habitAssignService.unenrollHabit(habitAssignId, userVO.getId(), date))
                .thenReturn(habitAssignDto);

        mockMvc.perform(post(link + "/{habitAssignId}/unenroll/{date}", habitAssignId, date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).unenrollHabit(habitAssignId, userVO.getId(), date);
    }

    @Test
    void getInprogressHabitAssignOnDate() throws Exception {
        UserVO userVO = prepareMockUser();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(1L).build();

        when(habitAssignService.findInprogressHabitAssignsOnDate(userVO.getId(), date, locale.getLanguage()))
                .thenReturn(List.of(habitAssignDto));

        mockMvc.perform(get(link + "/active/{date}", date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).findInprogressHabitAssignsOnDate(userVO.getId(), date, locale.getLanguage());
    }

    @Test
    void getHabitAssignBetweenDates() throws Exception {
        UserVO userVO = prepareMockUser();
        LocalDate from = LocalDate.now().minusDays(3);
        LocalDate enrollDate = LocalDate.now().minusDays(2);
        LocalDate to = LocalDate.now();

        HabitsDateEnrollmentDto habitsDateEnrollmentDto = HabitsDateEnrollmentDto
                .builder().habitAssigns(List.of()).enrollDate(enrollDate).build();

        when(habitAssignService.findHabitAssignsBetweenDates(userVO.getId(), from, to, locale.getLanguage()))
                .thenReturn(List.of(habitsDateEnrollmentDto));

        mockMvc.perform(get(link + "/activity/{from}/to/{to}", from, to)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(userVO.getId(), from, to, locale.getLanguage());
    }

    @Test
    void cancelHabitAssign() throws Exception {
        Long habitId = 1L;
        UserVO userVO = prepareMockUser();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(1L).build();

        when(habitAssignService.cancelHabitAssign(habitId, userVO.getId())).thenReturn(habitAssignDto);

        mockMvc.perform(patch(link + "/cancel/{habitId}", habitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).cancelHabitAssign(habitId, userVO.getId());
    }

    @Test
    void deleteHabitAssign() throws Exception {
        Long habitAssignId = 1L;
        UserVO userVO = prepareMockUser();

        doNothing().when(habitAssignService).deleteHabitAssign(habitAssignId, userVO.getId());

        mockMvc.perform(delete(link + "/delete/{habitAssignId}", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());
        verify(habitAssignService).deleteHabitAssign(habitAssignId, userVO.getId());
    }

    @Test
    void updateShoppingListStatus() throws Exception {
        UpdateUserShoppingListDto updateUserShoppingListDto = UpdateUserShoppingListDto.builder()
                .habitAssignId(1L)
                .userShoppingListItemId(1L)
                .userShoppingListAdvanceDto(List.of())
                .build();

        doNothing().when(habitAssignService).updateUserShoppingListItem(updateUserShoppingListDto);

        mockMvc.perform(put(link + "/saveShoppingListForHabitAssign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserShoppingListDto)))
                .andExpect(status().isOk());

        verify(habitAssignService).updateUserShoppingListItem(updateUserShoppingListDto);
    }

    @Test
    void updateProgressNotificationHasDisplayed() throws Exception {
        Long habitAssignId = 1L;
        UserVO userVO = prepareMockUser();


        doNothing().when(habitAssignService).updateProgressNotificationHasDisplayed(habitAssignId, userVO.getId());

        mockMvc.perform(put(link + "/{habitAssignId}/updateProgressNotificationHasDisplayed", habitAssignId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(() -> testEmail))
                .andExpect(status().isOk());

        verify(habitAssignService).updateProgressNotificationHasDisplayed(habitAssignId, userVO.getId());
    }

    private UserVO prepareMockUser() {
        UserVO userVO = UserVO.builder()
                .id(1L)
                .name("Tom")
                .email(testEmail)
                .build();
        when(userService.findByEmail(testEmail)).thenReturn(userVO);
        return userVO;
    }
}