package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.event.UploadEventImageDto;
import greencity.dto.event.UploadEventImagesDto;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
class EventServiceImplTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    EventRepo eventRepo;

    @Mock
    private EventImageRepo eventImageRepo;

    @Mock
    private FileService fileService;

    @InjectMocks
    EventServiceImpl eventService;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Long EVENT_ID = 1L;
    private final CreateEventRequestDto createEventRequestDto = ModelUtils.getCreateEventRequestDto();
    private final EventResponseDto eventResponseDto = ModelUtils.getEventResponseDto();
    private final Event event = ModelUtils.createEvent();

    @Test
    void createEvent() {
        when(modelMapper.map(createEventRequestDto, Event.class)).thenReturn(event);
        when(modelMapper.map(event, EventResponseDto.class)).thenReturn(eventResponseDto);
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

    @Test
    void uploadEventImage_success() {
        Event event = Event.builder().id(EVENT_ID).build();
        UploadEventImageDto dto = new UploadEventImageDto(mock(MultipartFile.class), true);

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(List.of());
        when(fileService.upload(any(MultipartFile.class))).thenReturn("http://image.url");
        EventImage saved = EventImage.builder().id(123L).url("http://image.url").isMain(true).event(event).build();
        when(eventImageRepo.save(any())).thenReturn(saved);

        EventImageDto expectedDto = new EventImageDto(123L, "http://image.url", true);
        when(modelMapper.map(any(EventImage.class), eq(EventImageDto.class))).thenReturn(expectedDto);

        EventImageDto result = eventService.uploadEventImage(dto, EVENT_ID);

        assertThat(result).isEqualTo(expectedDto);
        verify(eventImageRepo).save(any());
    }

    @Test
    void uploadEventImage_eventNotFound_throwsException() {
        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.empty());

        UploadEventImageDto dto = new UploadEventImageDto(mock(MultipartFile.class), true);

        assertThatThrownBy(() -> eventService.uploadEventImage(dto, EVENT_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Event not found");

        verify(eventRepo).findById(any());
    }

    @Test
    void uploadEventImage_tooManyImages_throwsException() {
        Event event = Event.builder().id(EVENT_ID).build();
        List<EventImage> existingImages = IntStream.range(0, 5)
                .mapToObj(i -> EventImage.builder().id((long) i).build())
                .toList();

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(1024L);

        UploadEventImageDto dto = new UploadEventImageDto(file, false);

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(existingImages);

        assertThatThrownBy(() -> eventService.uploadEventImage(dto, EVENT_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Maximum of 5 images allowed");

        verify(eventRepo).findById(EVENT_ID);
        verify(eventImageRepo).findAllByEventId(EVENT_ID);
    }

    @Test
    void uploadEventImage_multipleMainImages_throwsException() {
        Event event = Event.builder().id(EVENT_ID).build();
        List<EventImage> existingImages = List.of(EventImage.builder().isMain(true).build());

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(existingImages);

        UploadEventImageDto dto = new UploadEventImageDto(mock(MultipartFile.class), true);
        Set<ConstraintViolation<UploadEventImageDto>> violations = validator.validate(dto);

        assertThatThrownBy(() -> eventService.uploadEventImage(dto, EVENT_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only one main image is allowed");

        assertThat(violations).isNotNull();
        assertThat(violations.iterator().next().getMessage()).contains("Invalid image. Only JPG/PNG allowed and max size is 10MB.");
    }

    @Test
    void uploadEventImages_success() {
        Event event = Event.builder().id(EVENT_ID).build();
        UploadEventImageDto dto1 = new UploadEventImageDto(mock(MultipartFile.class), false);
        UploadEventImageDto dto2 = new UploadEventImageDto(mock(MultipartFile.class), true);

        List<UploadEventImageDto> dtos = List.of(dto1, dto2);

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(List.of());

        when(fileService.upload(any())).thenReturn("http://image.url");
        when(eventImageRepo.save(any())).thenReturn(EventImage.builder().id(1L).url("http://image.url").isMain(true).event(event).build());
        when(modelMapper.map(any(EventImage.class), eq(EventImageDto.class)))
                .thenReturn(new EventImageDto(1L, "http://image.url", true));

        List<EventImageDto> result = eventService.uploadEventImages(
                new UploadEventImagesDto(dtos), EVENT_ID
        );

        assertThat(result.size()).isEqualTo(2);

        verify(eventImageRepo, times(2)).save(any());
    }
}
