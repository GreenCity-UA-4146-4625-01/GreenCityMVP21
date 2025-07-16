package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private final CreateEventRequestDto createEventRequestDto = ModelUtils.getCreateEventRequestDto();
    private final EventResponseDto eventResponseDto = ModelUtils.getEventResponseDto();
    private final Event event = ModelUtils.createEvent();

    @Test
    void createEvent() {
        when(modelMapper.map(createEventRequestDto, Event.class)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);
        when(eventRepo.save(event)).thenReturn(event);

        EventResponseDto result = eventService.createEvent(createEventRequestDto, new UserVO().setId(1L));

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
    void getAllEvents_returnsCorrectPageableDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event, event), pageable, 2);

        when(eventRepo.findAll(pageable)).thenReturn(eventPage);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        // when
        PageableDto<EventResponseDto> result = eventService.getAllEvents(pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getPage().size());
        assertEquals("Test", result.getPage().get(0).getTitle());
        assertEquals("Test", result.getPage().get(1).getTitle());
        assertEquals(0, result.getCurrentPage());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

}
