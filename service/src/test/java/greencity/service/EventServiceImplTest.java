package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventLocationDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventLocation;
import greencity.entity.User;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventServiceImplTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    EventRepo eventRepo;

    @Mock
    EventImageService eventImageService;

    @InjectMocks
    EventServiceImpl eventService;

    private final CreateEventRequestDto createEventRequestDto = ModelUtils.getCreateEventRequestDto();
    private final EventResponseDto eventResponseDto = ModelUtils.getEventResponseDto();
    private final Event event = ModelUtils.createEvent();
    private final UserVO userVO = ModelUtils.getUserVO();

    @Test
    void createEvent_withImages_success() {
        List<MultipartFile> images = List.of(mock(MultipartFile.class), mock(MultipartFile.class));

        when(modelMapper.map(createEventRequestDto, Event.class)).thenReturn(event);
        when(modelMapper.map(userVO, User.class)).thenReturn(event.getCreator());
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto result = eventService.createEvent(createEventRequestDto, userVO, images);

        assertNotNull(result);
        assertEquals("Test", result.getTitle());
        verify(eventRepo).save(event);
        verify(eventImageService).uploadEventImages(images, event.getId(), userVO);
    }

    @Test
    void createEvent_noImages_success() {
        when(modelMapper.map(createEventRequestDto, Event.class)).thenReturn(event);
        when(modelMapper.map(userVO, User.class)).thenReturn(event.getCreator());
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto result = eventService.createEvent(createEventRequestDto, userVO, null);

        assertNotNull(result);
        assertEquals("Test", result.getTitle());
        verify(eventRepo).save(event);
        verify(eventImageService, org.mockito.Mockito.never()).uploadEventImages(any(), anyLong(), any());
    }

    @Test
    void getEventById() {
        when(eventRepo.findEventById(event.getId())).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto result = eventService.getEventById(event.getId());

        assertNotNull(result);
        assertEquals(event.getId(), result.getEventId());
        verify(eventRepo).findEventById(event.getId());
    }

    @Test
    void getAllEvents_returnsCorrectPageableDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event, event), pageable, 2);

        when(eventRepo.findAll(pageable)).thenReturn(eventPage);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        PageableDto<EventResponseDto> result = eventService.getAllEvents(pageable);

        assertNotNull(result);
        assertEquals(2, result.getPage().size());
        assertEquals("Test", result.getPage().get(0).getTitle());
        assertEquals("Test", result.getPage().get(1).getTitle());
        assertEquals(0, result.getCurrentPage());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void assignUserToEvent_success() {
        when(eventRepo.findEventById(event.getId())).thenReturn(Optional.of(event));
        when(modelMapper.map(userVO, User.class)).thenReturn(ModelUtils.getUser());
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto result = eventService.assignUserToEvent(event.getId(), userVO);

        assertNotNull(result);
        verify(eventRepo).save(event);
    }


    @Test
    void unassignUserFromEvent_success() {
        User participant = ModelUtils.getUser();
        participant.setId(userVO.getId());

        event.getParticipants().add(participant);

        when(eventRepo.findEventById(event.getId())).thenReturn(Optional.of(event));
        when(modelMapper.map(userVO, User.class)).thenReturn(participant);
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        EventResponseDto result = eventService.unassignUserFromEvent(event.getId(), userVO);

        assertNotNull(result);
        verify(eventRepo).save(event);
    }

    @Test
    void getEventsAssignedToUser_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> eventPage = new PageImpl<>(List.of(event), pageable, 1);

        when(eventRepo.findByParticipants_Id(userVO.getId(), pageable)).thenReturn(eventPage);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);

        PageableDto<EventResponseDto> result = eventService.getEventsAssignedToUser(userVO, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals("Test", result.getPage().get(0).getTitle());

        verify(eventRepo).findByParticipants_Id(userVO.getId(), pageable);
        verify(modelMapper).map(event, EventResponseDto.class);
    }

    @Test
    void updateLocationByEventId_ShouldReturnUpdatedEventResponseDto() {
        Long eventId = 1L;
        UserVO user = ModelUtils.getUserVO();
        EventLocationDto locationDto = ModelUtils.getEventLocationDto();

        Event event = new Event();
        event.setId(eventId);
        event.setEventLocations(new ArrayList<>());
        event.setCreator(ModelUtils.getUser());

        EventLocation eventLocation = new EventLocation();
        eventLocation.setAddress(locationDto.getAddress());
        eventLocation.setLatitude(locationDto.getLatitude());
        eventLocation.setLongitude(locationDto.getLongitude());
        eventLocation.setEvent(event);

        Event savedEvent = new Event();
        savedEvent.setId(eventId);
        savedEvent.setEventLocations(List.of(eventLocation));

        EventResponseDto responseDto = ModelUtils.getEventResponseDto();

        when(eventRepo.findEventById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.map(locationDto, EventLocation.class)).thenReturn(eventLocation);
        when(eventRepo.save(any(Event.class))).thenReturn(savedEvent);
        when(modelMapper.map(savedEvent, EventResponseDto.class)).thenReturn(responseDto);

        EventResponseDto result = eventService.updateLocationByEventId(eventId, locationDto, user);

        assertNotNull(result);
        assertEquals(responseDto, result);

        verify(eventRepo).findEventById(eventId);
        verify(modelMapper).map(locationDto, EventLocation.class);
        verify(eventRepo).save(event);
        verify(modelMapper).map(savedEvent, EventResponseDto.class);
    }
}
