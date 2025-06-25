package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.constant.ErrorMessage;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.HabitService;
import greencity.service.TagsService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitControllerTest {

    private static final String baseUrl = "/habit";
    @InjectMocks
    HabitController habitController;
    @Mock
    HabitService habitService;
    @MockBean
    TagsService tagsService;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        ErrorAttributes errorAttributes = new DefaultErrorAttributes();

        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    void findHabitByIdWithLocaleTranslation_whenHabitExists() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        HabitDto expectedHabitDto = new HabitDto();
        expectedHabitDto.setId(habitId);
        expectedHabitDto.setHabitTranslation(new HabitTranslationDto().setLanguageCode(locale.getLanguage()));

        when(habitService.getByIdAndLanguageCode(habitId, locale.getLanguage())).thenReturn(expectedHabitDto);

        mockMvc.perform(get(baseUrl + "/" + habitId)
            .locale(locale)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.habitTranslation.languageCode").value(locale.getLanguage()))
            .andExpect(jsonPath("$.id").value(habitId));

        verify(habitService).getByIdAndLanguageCode(habitId, locale.getLanguage());

    }

    @Test
    void findHabitByIdWithLocaleTranslation_whenHabitNotFound404() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;
        String errorMessage = ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId;

        when(habitService.getByIdAndLanguageCode(habitId, locale.getLanguage()))
            .thenThrow(NotFoundException.class);

        mockMvc.perform(get(baseUrl + "/" + habitId)
            .locale(locale)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(habitService).getByIdAndLanguageCode(habitId, locale.getLanguage());
    }

}
