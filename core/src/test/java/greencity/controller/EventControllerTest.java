package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.GreenCityApplication;
import greencity.config.SecurityConfig;
import greencity.dto.event.EditEventRequestDto;
import greencity.dto.event.EventDateTimeDto;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.EventLocationDto;
import greencity.dto.event.EventResponseDto;
import greencity.enums.EventType;
import greencity.enums.EventVisibility;
import greencity.security.jwt.JwtTool;
import greencity.service.EventService;
import greencity.dto.user.UserVO;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test class for {@link EventController} that verifies the behavior*/
@WebMvcTest(EventController.class)
@ContextConfiguration(classes = {GreenCityApplication.class, SecurityConfig.class, JwtTool.class})
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void editEvent_Returns200AndUpdatedEventDto() throws Exception {
        Long eventId = 1L;
        UserVO mockUser = UserVO.builder().id(100L).name("Test User").build();

        EditEventRequestDto editRequest = EditEventRequestDto.builder()
                .eventId(eventId)
                .title("Updated Title")
                .description("Updated description for the event that is longer than 20 characters.")
                .visibility(EventVisibility.OPEN)
                .eventTypes(Set.of(EventType.PLACE))
                .eventDateTimes(List.of(EventDateTimeDto.builder()
                        .date(LocalDate.now().plusDays(1))
                        .startTime(LocalTime.now().plusHours(1))
                        .endTime(LocalTime.now().plusHours(3))
                        .allDay(false)
                        .build()))
                .locations(List.of(EventLocationDto.builder()
                        .address("Kyiv, Ukraine")
                        .latitude(50.45)
                        .longitude(30.523)
                        .build()))
                .onlineLinks(List.of("https://example.com"))
                .images(List.of(EventImageDto.builder()
                        .imageId(123L)
                        .url("https://example.com/image.jpg")
                        .isMain(true)
                        .build()))
                .mainImageId(123L)
                .build();


        EventResponseDto response = EventResponseDto.builder()
                .eventId(eventId)
                .title(editRequest.getTitle())
                .description(editRequest.getDescription())
                .visibility(editRequest.getVisibility())
                .eventTypes(editRequest.getEventTypes())
                .eventDateTimes(editRequest.getEventDateTimes())
                .locations(editRequest.getLocations())
                .onlineLinks(editRequest.getOnlineLinks())
                .images(editRequest.getImages())
                .mainImageId(editRequest.getMainImageId())
                .createdAt(LocalDateTime.now())
                .build();

        when(eventService.updateEventById(eq(eventId), any(EditEventRequestDto.class), any()))
                .thenReturn(response);

        var response1 = mockMvc.perform(patch("/events/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(editRequest)))
                .andExpect(status().isOk());
                response1.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated description for the event that is longer than 20 characters."))
                .andExpect(jsonPath("$.visibility").value("OPEN"))
                .andExpect(jsonPath("$.eventTypes[0]").value("PLACE"))
                .andExpect(jsonPath("$.locations[0].address").value("Kyiv, Ukraine"))
                .andExpect(jsonPath("$.onlineLinks[0]").value("https://example.com"))
                .andExpect(jsonPath("$.images[0].url").value("https://example.com/image.jpg"));

        verify(eventService, times(1)).updateEventById(eq(eventId), any(EditEventRequestDto.class), any());
    }
}
