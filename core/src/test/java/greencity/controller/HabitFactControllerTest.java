package greencity.controller;

import greencity.dto.PageableDto;
import greencity.dto.habitfact.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.service.HabitFactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {

    @Mock
    private HabitFactService habitFactService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HabitFactController habitFactController;

    private static final Long HABIT_ID = 1L;
    private static final Long LANGUAGE_ID = 1L;
    private static final Long FACT_ID = 1L;
    private static final Long INVALID_FACT_ID = 999L;
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);
    private static final Locale LOCALE = Locale.ENGLISH;

    private HabitFactPostDto habitFactPostDto;
    private HabitFactUpdateDto habitFactUpdateDto;
    private LanguageTranslationDTO languageTranslationDTO;
    private HabitFactDtoResponse habitFactDtoResponse;
    private HabitFactVO habitFactVO;

    @BeforeEach
    void setUp() {
        habitFactPostDto = HabitFactPostDto.builder()
                .translations(List.of(new LanguageTranslationDTO()))
                .habit(new HabitIdRequestDto(HABIT_ID))
                .build();

        habitFactUpdateDto = HabitFactUpdateDto.builder()
                .translations(List.of(new HabitFactTranslationUpdateDto()))
                .habit(new HabitIdRequestDto(HABIT_ID))
                .build();

        languageTranslationDTO = new LanguageTranslationDTO();
        languageTranslationDTO.setContent("Test content");

        habitFactDtoResponse = HabitFactDtoResponse.builder()
                .id(FACT_ID)
                .habit(null)
                .translations(List.of(new HabitFactTranslationDto()))
                .build();

        habitFactVO = HabitFactVO.builder()
                .id(FACT_ID)
                .habit(null)
                .translations(List.of(new HabitFactTranslationVO()))
                .build();
    }

    @Test
    void getRandomFactByHabitId_Success() {
        when(habitFactService.getRandomHabitFactByHabitIdAndLanguage(HABIT_ID, LOCALE.getLanguage()))
                .thenReturn(languageTranslationDTO);

        LanguageTranslationDTO result = habitFactController.getRandomFactByHabitId(HABIT_ID, LOCALE);

        assertNotNull(result);
        assertEquals(languageTranslationDTO, result);
        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(HABIT_ID, LOCALE.getLanguage());
    }

    @Test
    void getHabitFactOfTheDay_Success() {
        when(habitFactService.getHabitFactOfTheDay(LANGUAGE_ID)).thenReturn(languageTranslationDTO);

        LanguageTranslationDTO result = habitFactController.getHabitFactOfTheDay(LANGUAGE_ID);

        assertNotNull(result);
        assertEquals(languageTranslationDTO, result);
        verify(habitFactService).getHabitFactOfTheDay(LANGUAGE_ID);
    }

    @Test
    void getAll_Success() {
        PageableDto<LanguageTranslationDTO> pageableDto = new PageableDto<>(
                List.of(languageTranslationDTO), 1, 0, 1
        );

        when(habitFactService.getAllHabitFacts(PAGEABLE, LOCALE.getLanguage())).thenReturn(pageableDto);

        ResponseEntity<PageableDto<LanguageTranslationDTO>> response =
                habitFactController.getAll(PAGEABLE, LOCALE);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pageableDto, response.getBody());
        verify(habitFactService).getAllHabitFacts(PAGEABLE, LOCALE.getLanguage());
    }

    @Test
    void save_Success() {
        when(habitFactService.save(habitFactPostDto)).thenReturn(habitFactVO);
        when(modelMapper.map(habitFactVO, HabitFactDtoResponse.class)).thenReturn(habitFactDtoResponse);

        ResponseEntity<HabitFactDtoResponse> response = habitFactController.save(habitFactPostDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(habitFactDtoResponse, response.getBody());
        verify(habitFactService).save(habitFactPostDto);
        verify(modelMapper).map(habitFactVO, HabitFactDtoResponse.class);
    }

    @Test
    void update_Success() {
        when(habitFactService.update(habitFactUpdateDto, FACT_ID)).thenReturn(habitFactVO);
        when(modelMapper.map(habitFactVO, HabitFactPostDto.class)).thenReturn(habitFactPostDto);

        ResponseEntity<HabitFactPostDto> response = habitFactController.update(habitFactUpdateDto, FACT_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(habitFactPostDto, response.getBody());
        verify(habitFactService).update(habitFactUpdateDto, FACT_ID);
        verify(modelMapper).map(habitFactVO, HabitFactPostDto.class);
    }

    @Test
    void delete_Success() {
        when(habitFactService.delete(FACT_ID)).thenReturn(FACT_ID);

        ResponseEntity<Object> response = habitFactController.delete(FACT_ID);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(habitFactService).delete(FACT_ID);
    }

    @Test
    void delete_BadRequest_WhenInvalidId() {
        when(habitFactService.delete(INVALID_FACT_ID))
                .thenThrow(new IllegalArgumentException(String.format("Habit fact with id %d not found", INVALID_FACT_ID)));

        assertThrows(IllegalArgumentException.class, () -> habitFactController.delete(INVALID_FACT_ID));
        verify(habitFactService).delete(INVALID_FACT_ID);
    }
}