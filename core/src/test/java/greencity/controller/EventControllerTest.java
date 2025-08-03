package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.config.MockCurrentUserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.event.*;
import greencity.dto.user.UserVO;
import greencity.enums.Role;
import greencity.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(eventService.createEvent(any(CreateEventRequestDto.class), any(UserVO.class), any()))
                .thenReturn(responseDto);

        MockMultipartFile eventPart = new MockMultipartFile(
                "event",
                "event.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        mockMvc.perform(multipart("/events")
                        .file(eventPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(eventService).createEvent(any(CreateEventRequestDto.class), any(UserVO.class), any());
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

    @Test
    void joinEvent_ShouldReturn200() throws Exception {
        Long eventId = 1L;
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(eventService.assignUserToEvent(eq(eventId), any(UserVO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/events/{eventId}/join", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        verify(eventService).assignUserToEvent(eq(eventId), any(UserVO.class));
    }

    @Test
    void leaveEvent_ShouldReturn200() throws Exception {
        Long eventId = 1L;
        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(eventService.unassignUserFromEvent(eq(eventId), any(UserVO.class))).thenReturn(responseDto);

        mockMvc.perform(delete("/events/{eventId}/leave", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(eventService).unassignUserFromEvent(eq(eventId), any(UserVO.class));
    }

    @Test
    void getEventsAssignedToUser_ShouldReturn200() throws Exception {
        PageableDto<EventResponseDto> pageableDto = new PageableDto<>(Collections.emptyList(), 0, 0, 0);

        when(eventService.getEventsAssignedToUser(any(UserVO.class), any(Pageable.class)))
                .thenReturn(pageableDto);

        mockMvc.perform(get("/events/assigned")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(eventService).getEventsAssignedToUser(any(UserVO.class), any(Pageable.class));
    }

    @Test
    void updateEventLocation_ShouldReturnEventResponse() {
        Long eventId = 1L;
        EventLocationDto locationDto = ModelUtils.getEventLocationDto();
        EventResponseDto expectedResponse = ModelUtils.getEventResponseDto();
        UserVO mockUser = ModelUtils.getUserVO();

        when(eventService.updateLocationByEventId(eventId, locationDto, mockUser))
                .thenReturn(expectedResponse);

        EventResponseDto actual = eventController.updateLocation(eventId, locationDto, mockUser).getBody();

        assertEquals(expectedResponse, actual);
        verify(eventService).updateLocationByEventId(eventId, locationDto, mockUser);
    }

    @Test
    void editEvent_ShouldReturn200() throws Exception {
        Long eventId = 1L;

        EditEventRequestDto dto = EditEventRequestDto.builder()
                .title("Updated Event title")
                .build();

        EventResponseDto responseDto = EventResponseDto.builder()
                .eventId(eventId)
                .title("Updated Event title")
                .build();

        UserVO mockUser = UserVO.builder()
                .id(100L)
                .email("admin@example.com")
                .role(Role.ROLE_ADMIN)
                .build();

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .setCustomArgumentResolvers(new MockCurrentUserArgumentResolver(mockUser))
                .build();

        MockMultipartFile eventPart = new MockMultipartFile(
                "event",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(dto)
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "images", "image.jpg", "image/jpeg", "fake-image-content".getBytes()
        );

        when(eventService.updateEventById(eq(eventId), any(), any(), any()))
                .thenReturn(responseDto);

        mockMvc.perform(multipart("/events/{id}", eventId)
                        .file(eventPart)
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.title").value("Updated Event title"));

        verify(eventService, times(1)).updateEventById(eq(eventId), any(), any(), any());
    }

    @Test
    void deleteEvent_ShouldReturn200_WithSpecificUser() throws Exception {
        Long eventId = 1L;

        UserVO mockUser = UserVO.builder()
                .id(10L)
                .email("example@email.com")
                .role(Role.ROLE_ADMIN)
                .build();

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(eventController)
                .setCustomArgumentResolvers(new MockCurrentUserArgumentResolver(mockUser))
                .build();

        doNothing().when(eventService).deleteEventById(eq(eventId), any(UserVO.class));

        mockMvc.perform(delete("/events/{id}", eventId))
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteEventById(eq(eventId), eq(mockUser));
    }

    @Test
    void searchEvent_ShouldReturn200() throws Exception {
        String query = "Tree";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));

        EventPreviewDto event1 = EventPreviewDto.builder()
                .id(1L)
                .title("Tree")
                .build();

        EventPreviewDto event2 = EventPreviewDto.builder()
                .id(2L)
                .title("Save the Trees")
                .build();

        List<EventPreviewDto> content = List.of(event1, event2);
        PageableDto<EventPreviewDto> response = new PageableDto<>(
                content, content.size(), 0, 1
        );

        when(eventService.searchEventsByTitle(eq(query), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/events/search")
                .param("query", query)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "title")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.page[0].title").value("Tree"))
                .andExpect(jsonPath("$.page[1].title").value("Save the Trees"));

        verify(eventService, times(1)).searchEventsByTitle(eq(query), any(Pageable.class));
    }
}
