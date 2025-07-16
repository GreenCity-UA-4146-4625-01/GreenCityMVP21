package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventDateTimeDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void createEvent_ShouldReturn201() throws Exception {
        CreateEventRequestDto requestDto = ModelUtils.getCreateEventRequestDto();
        requestDto.setEventDateTimes(List.of(
                new EventDateTimeDto(LocalDate.now().plusDays(1), LocalTime.now().plusMinutes(10), LocalTime.now().plusMinutes(20), false),
                new EventDateTimeDto(LocalDate.now().plusDays(1), LocalTime.now().plusMinutes(20), LocalTime.now().plusMinutes(20), false)
        ));
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(eventService.createEvent(any(CreateEventRequestDto.class), any(UserVO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated());

        verify(eventService).createEvent(any(CreateEventRequestDto.class), any(UserVO.class));
    }

    @Test
    void getEventById_ShouldReturn200() throws Exception {
        Long id = 1L;
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(eventService.getEventById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/events/{id}", id))
                .andExpect(status().isOk());

        verify(eventService).getEventById(id);
    }

    @Test
    void getAllEvents_ShouldReturn200() throws Exception {
        PageableDto<EventResponseDto> pageableDto = new PageableDto<>(Collections.emptyList(), 0, 0, 0);

        when(eventService.getAllEvents(any(Pageable.class))).thenReturn(pageableDto);

        mockMvc.perform(get("/events")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(eventService).getAllEvents(any(Pageable.class));
    }
}
