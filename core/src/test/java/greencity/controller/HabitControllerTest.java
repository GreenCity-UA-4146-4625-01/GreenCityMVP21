package greencity.controller;

import com.azure.core.http.HttpResponse;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.service.HabitService;
import greencity.service.TagsService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test for {@link HabitController}.
 *
 * <p><b>Tested endpoints:</b></p>
 *
 * <ul>
 *     {@code GET /habit/{id}} – Get habit by ID (localized)
 *      <li>
 *          {@link HabitControllerTest#findHabitByIdWithLocaleTranslation_whenHabitExists()}
 *      <li>
 *          {@link HabitControllerTest#findHabitByIdWithLocaleTranslation_whenHabitNotFound404()}
 *      </li>
 *      <li>
 *          {@link HabitControllerTest#findHabitByIdWithLocaleTranslation_whenBadRequest400()}
 *      </li>
 * </ul>
 *
 * <ul>GET /habit – Get all habits for current user</ul>
 * <ul>GET /habit/{id}/shopping-list – Get shopping list items for a habit</ul>
 * <ul>GET /habit/tags/search – Get habits by tags and language code</ul>
 * <ul>GET /habit/search – Filter habits by tags, isCustomHabit, complexities</ul>
 * <ul>GET /habit/tags – Get all habit tags</ul>
 * <ul>POST /habit/custom – Add new custom habit with multipart image</ul>
 * <ul>GET /habit/{habitId}/friends/profile-pictures – Get profile pictures of friends assigned to habit</ul>
 */

@ExtendWith(MockitoExtension.class)
public class HabitControllerTest {

    private static final String baseUrl = "/habit";
    @InjectMocks
    HabitController habitController;
    @Mock
    HabitService habitService;
    @Mock
    UserService userService;
    @MockBean
    TagsService tagsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    @DisplayName("GET /habit/{id} returns existing DTO when habit is found")
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
    @DisplayName("GET /habit/{id} returns '404 - Not Found' when habit is not found")
    void findHabitByIdWithLocaleTranslation_whenHabitNotFound404() throws Exception {
        Long habitId = 1L;
        Locale locale = Locale.ENGLISH;

        when(habitService.getByIdAndLanguageCode(habitId, locale.getLanguage()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get(baseUrl + "/" + habitId)
                        .locale(locale)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(habitService).getByIdAndLanguageCode(habitId, locale.getLanguage());
    }

    @Test
    @DisplayName("GET /habit/{id} returns '400 - Bad Request' when passed wrong @id")
    void findHabitByIdWithLocaleTranslation_whenBadRequest400() throws Exception {
        String habitId = "abc";
        Locale locale = Locale.ENGLISH;

        mockMvc.perform(get(baseUrl + "/" + habitId)
                        .locale(locale)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(habitService);
    }

    @Test
    @DisplayName("GET /habit returns all default and custom habits")
    void shouldReturnPageableDtoOfHabitDto_whenFindAllHabitsCalled() throws Exception {

        Locale locale = Locale.ENGLISH;
        List<HabitDto> habits = List.of(new HabitDto().setImage("https://csb10032000a548f571.blob.core.windows.net/allfiles/304ff73c-7e6d-4a17-be7d-59fc3666d351931fb71c088a926a1e04b6896d109fa2.jpg"));
        PageableDto<HabitDto> page = new PageableDto<>(habits, 1, 0, 1);
        when(habitService.getAllHabitsByLanguageCode(any(UserVO.class), any(Pageable.class), eq(locale.getLanguage()))).thenReturn(page);

         mockMvc.perform(get(baseUrl)
                         .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("@.page[0].image").value("https://csb10032000a548f571.blob.core.windows.net/allfiles/304ff73c-7e6d-4a17-be7d-59fc3666d351931fb71c088a926a1e04b6896d109fa2.jpg"));

         verify(habitService).getAllHabitsByLanguageCode(any(UserVO.class), any(Pageable.class), eq(locale.getLanguage()));
    }

    @Test
    @DisplayName("GET /habit returns '400 - Bad Request' when no habits found")
    void shouldReturnPageableDtoOfHabitDto_whenNoHabitsFound() throws Exception {
        Locale locale = Locale.ENGLISH;
        List<HabitDto> habits = List.of();
        PageableDto<HabitDto> page = new PageableDto<>(habits, 1, 0, 1);

        when(habitService.getAllHabitsByLanguageCode(any(UserVO.class), any(Pageable.class), eq(locale.getLanguage()))).thenReturn(page);

        mockMvc.perform(get(baseUrl)
                .locale(locale)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
