package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.enums.EventVisibility;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {
    @Mock
    private MockMvc mockMvc;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EventService eventService;
    @Mock
    private UserService userService;

    @InjectMocks
    private EventController eventController;

    private final String link = "/events";
    private final Long id = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorAttributes errorAttributes = new DefaultErrorAttributes();


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void testCreateEvent() throws Exception {
        CreateEventRequestDto requestDto = CreateEventRequestDto.builder()
                .title("Test")
                .description("Test Test Test Test Test Test")
                .visibility(EventVisibility.OPEN)
                .eventTypes(Set.of())
                .eventDateTimes(List.of())
                .build();

        EventResponseDto responseDto = modelMapper.map(requestDto, EventResponseDto.class);

        Mockito.when(eventService.createEvent(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post(link)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(1L));

        verify(eventService).createEvent(requestDto);
    }

    @Test
    void testGetEventById() throws Exception {
        EventResponseDto responseDto = new EventResponseDto();
        responseDto.setEventId(id);

        Mockito.when(eventService.getEventById(id)).thenReturn(responseDto);

        mockMvc.perform(get(link + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(1L));

        verify(eventService).getEventById(id);
    }

    @Test
    void testGetAllEvents() throws Exception {
        EventResponseDto event1 = new EventResponseDto();
        event1.setEventId(id);

        EventResponseDto event2 = new EventResponseDto();
        event2.setEventId(2L);

        PageableDto<EventResponseDto> pageableDto = new PageableDto<>(
                List.of(event1, event2),
                2, 0, 1
        );

        Mockito.when(eventService.getAllEvents(any())).thenReturn(pageableDto);

        mockMvc.perform(get(link))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.elements", hasSize(2)));

        verify(eventService).getAllEvents(any());
    }
}
