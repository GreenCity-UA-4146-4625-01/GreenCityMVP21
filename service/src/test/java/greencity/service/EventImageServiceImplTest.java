package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.UploadEventImageDto;
import greencity.dto.event.UploadEventImagesDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventImageRepo;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventImageServiceImplTest {

    @Mock
    private EventRepo eventRepo;
    @Mock
    private EventImageRepo eventImageRepo;
    @Mock
    private FileService fileService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EventImageServiceImpl eventImageService;

    private final Long EVENT_ID = 1L;
    private final UserVO user = ModelUtils.getUserVO();

    private Event mockEvent() {
        return Event.builder().id(EVENT_ID).creator(ModelUtils.getUser()).build();
    }

    @Test
    void uploadEventImage_success() {
        UploadEventImageDto dto = new UploadEventImageDto(mock(MultipartFile.class), true);
        Event event = mockEvent();
        EventImage image = EventImage.builder().id(123L).url("url").isMain(true).event(event).build();
        EventImageDto expectedDto = new EventImageDto(123L, "url", true);

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(List.of());
        when(fileService.upload(any())).thenReturn("url");
        when(eventImageRepo.saveAll(any())).thenReturn(List.of(image));
        when(modelMapper.map(any(EventImage.class), eq(EventImageDto.class))).thenReturn(expectedDto);

        EventImageDto result = eventImageService.uploadEventImage(dto, EVENT_ID, user);

        assertThat(result).isEqualTo(expectedDto);
        verify(eventImageRepo).saveAll(any());
    }

    @Test
    void uploadEventImage_eventNotFound() {
        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventImageService.uploadEventImage(
                new UploadEventImageDto(mock(MultipartFile.class), true), EVENT_ID, user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void uploadEventImage_tooManyImages() {
        Event event = mockEvent();
        List<EventImage> existing = IntStream.range(0, 5)
                .mapToObj(i -> EventImage.builder().id((long) i).build()).toList();

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(existing);

        assertThatThrownBy(() -> eventImageService.uploadEventImage(
                new UploadEventImageDto(mock(MultipartFile.class), false), EVENT_ID, user))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void uploadEventImage_tooManyMainImages() {
        Event event = mockEvent();
        List<EventImage> existing = List.of(EventImage.builder().isMain(true).build());

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(existing);

        assertThatThrownBy(() -> eventImageService.uploadEventImage(
                new UploadEventImageDto(mock(MultipartFile.class), true), EVENT_ID, user))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only one main image is allowed");
    }

    @Test
    void uploadEventImage_noPermission() {
        UserVO anotherUser = UserVO.builder().id(99L).role(Role.ROLE_USER).build();
        Event event = mockEvent();

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventImageService.uploadEventImage(
                new UploadEventImageDto(mock(MultipartFile.class), true), EVENT_ID, anotherUser))
                .isInstanceOf(UserHasNoPermissionToAccessException.class);
    }

    @Test
    void uploadEventImages_success() {
        Event event = mockEvent();
        UploadEventImageDto dto1 = new UploadEventImageDto(mock(MultipartFile.class), false);
        UploadEventImageDto dto2 = new UploadEventImageDto(mock(MultipartFile.class), true);

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(List.of());
        when(fileService.upload(any())).thenReturn("url");
        when(eventImageRepo.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(EventImage.class), eq(EventImageDto.class)))
                .thenAnswer(invocation -> {
                    EventImage img = invocation.getArgument(0);
                    return new EventImageDto(img.getId(), img.getUrl(), img.getIsMain());
                });

        List<EventImageDto> result = eventImageService.uploadEventImages(
                new UploadEventImagesDto(List.of(dto1, dto2)), EVENT_ID, user);

        assertThat(result).hasSize(2);
        verify(eventImageRepo).saveAll(any());
    }
}
