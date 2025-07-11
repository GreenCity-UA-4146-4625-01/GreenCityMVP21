package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.entity.Event;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class EventServiceImplTest {
    @Mock
    ModelMapper modelMapper;
    @Mock
    EventRepo eventRepo;
    @InjectMocks
    EventServiceImpl eventService;

    private CreateEventRequestDto createEventRequestDto = ModelUtils.getCreateEventRequestDto();
    private EventResponseDto eventResponseDto = ModelUtils.getEventResponseDto();
    private Event event = ModelUtils.createEvent();

    @Test
    void createEvent() {
        when(modelMapper.map(createEventRequestDto, Event.class)).thenReturn(event);
        when(modelMapper.map(createEventRequestDto, EventResponseDto.class)).thenReturn(eventResponseDto);
        when(eventRepo.save(event)).thenReturn(event);

        EventResponseDto result = eventService.createEvent(createEventRequestDto);

        assertNotNull(result);
        assertEquals("Test", result.getTitle());
        verify(eventRepo).save(event);
    }

    @Test
    void getEventById() {
        when(eventRepo.findById(event.getId())).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto result = eventService.getEventById(event.getId());

        assertNotNull(result);
        assertEquals(event.getId(), result.getEventId());
        verify(eventRepo).findById(event.getId());
    }

    @Test
    void getAllEvents() {
        when(eventRepo.findAll()).thenReturn(List.of(event, event));
        when(modelMapper.map(eq(event), eq(EventResponseDto.class)))
                .thenReturn(EventResponseDto.builder().eventId(1L).title("Event 1").build());
        when(modelMapper.map(eq(event), eq(EventResponseDto.class)))
                .thenReturn(EventResponseDto.builder().eventId(1L).title("Event 1").build());

        List<EventResponseDto> result = eventService.getAllEvents();

        assertEquals(2, result.size());
        assertEquals("Event 1", result.get(0).getTitle());
        assertEquals("Event 1", result.get(1).getTitle());
        verify(eventRepo).findAll();
    }
}