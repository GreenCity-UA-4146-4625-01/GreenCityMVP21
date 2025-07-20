package greencity.service;

import greencity.ModelUtils;
import greencity.dto.event.EventImageDto;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventImage;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
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

    private static final Long EVENT_ID = 1L;
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

    private final UserVO user = ModelUtils.getUserVO();

    private Event mockEvent() {
        return Event.builder().id(EVENT_ID).creator(ModelUtils.getUser()).build();
    }

    @Test
    void uploadEventImages_success() {
        Event event = mockEvent();
        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);

        EventImage imageEntity1 = EventImage.builder().id(1L).url("url1").isMain(true).event(event).build();
        EventImage imageEntity2 = EventImage.builder().id(2L).url("url2").isMain(false).event(event).build();

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(List.of());
        when(fileService.upload(any())).thenReturn("url1", "url2");
        when(eventImageRepo.saveAll(any())).thenReturn(List.of(imageEntity1, imageEntity2));
        when(modelMapper.map(any(EventImage.class), eq(EventImageDto.class)))
                .thenAnswer(invocation -> {
                    EventImage img = invocation.getArgument(0);
                    return new EventImageDto(img.getId(), img.getUrl(), img.getIsMain());
                });

        List<MultipartFile> files = List.of(image1, image2);
        List<EventImageDto> result = eventImageService.uploadEventImages(files, EVENT_ID, user);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsMain()).isTrue();
        assertThat(result.get(1).getIsMain()).isFalse();

        verify(eventImageRepo).saveAll(any());
    }

    @Test
    void uploadEventImages_eventNotFound_throwsException() {
        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.empty());

        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file);

        assertThatThrownBy(() -> eventImageService.uploadEventImages(files, EVENT_ID, user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void uploadEventImages_tooManyImages_throwsException() {
        Event event = mockEvent();
        List<EventImage> existing = IntStream.range(0, 4)
                .mapToObj(i -> EventImage.builder().id((long) i).build())
                .toList();

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(existing);

        List<MultipartFile> newImages = List.of(
                mock(MultipartFile.class),
                mock(MultipartFile.class)
        );

        assertThatThrownBy(() -> eventImageService.uploadEventImages(newImages, EVENT_ID, user))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void uploadEventImages_emptyImageList_returnsEmptyList() {
        Event event = mockEvent();

        when(eventRepo.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(eventImageRepo.findAllByEventId(EVENT_ID)).thenReturn(List.of());

        List<MultipartFile> images = List.of();
        List<EventImageDto> result = eventImageService.uploadEventImages(images, EVENT_ID, user);

        assertThat(result).isEmpty();
    }
}