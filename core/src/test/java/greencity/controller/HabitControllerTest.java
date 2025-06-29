package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.PageableDto;
import greencity.dto.habit.AddCustomHabitDtoRequest;
import greencity.dto.habit.AddCustomHabitDtoResponse;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.shoppinglistitem.ShoppingListItemDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.service.HabitService;
import greencity.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for {@link HabitController}.
 *
 * <p><b>Tested endpoints:</b></p>
 *
 * <ul>
 *     {@code GET /habit/{id}} – Get habit by ID (localized)
 *      <li>
 *          {@link HabitControllerTest#shouldReturnExistingDtoWhenHabitIsFound()} ()}
 *      <li>
 *          {@link HabitControllerTest#shouldReturn404WhenPassedWrongId()}
 *      </li>
 * </ul>
 *
 * <ul>{@code GET /habit} – Get all habits for current user
 *      <li>
 *          {@link HabitControllerTest#shouldReturnPageableDtoOfHabitDto()}
 *      </li>
 * </ul>
 * <ul>{@code GET /habit/{id}/shopping-list} – Get shopping list items for a habit
 *      <li>
 *          {@link HabitControllerTest#shouldReturnShoppingListItemDto()}
 *      </li>
 * </ul>
 * <ul>{@code GET /habit/tags/search} – Get habits by tags and language code
 *      <li>
 *          {@link HabitControllerTest#shouldReturnPageableDtoByTags()}
 *      </li>
 * </ul>
 * <ul>{@code GET /habit/search} – Filter habits by tags, isCustomHabit, complexities
 *      <li>
 *          {@link HabitControllerTest#shouldReturnPageWithSearchResult()}
 *      </li>
 * </ul>
 * <ul>{@code GET /habit/tags} – Get all habit tags
 *      <li>
 *          {@link HabitControllerTest#shouldReturnAllHabitTags()}
 *      </li>
 * </ul>
 * <ul>{@code POST /habit/custom} – Add new custom habit with multipart image
 *      <li>
 *          {@link HabitControllerTest#shouldAddNewCustomHabitWithMultipartImage()}
 *      </li>
 * </ul>
 * <ul>{@code GET /habit/{HABIT_ID}/friends/profile-pictures} – Get profile pictures of friends assigned to habit
 *      <li>
 *          {@link HabitControllerTest#shouldReturnProfilePicturesOfFriendsAssignedToHabit()}
 *      </li>
 * </ul>
 */

@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "usergreencity@gmail.com", roles = {"USER"})
public class HabitControllerTest {

    public static final String IMG_EXAMPLE = "https://csb10032000a548f571.blob.core.windows.net/allfiles/304ff73c-7e6d-4a17-be7d-59fc3666d351931fb71c088a926a1e04b6896d109fa2.jpg";
    private static final String BASE_URL = "/habit";
    private final Locale LOCALE = Locale.ENGLISH;
    private final Long HABIT_ID = 1L;
    @InjectMocks
    HabitController habitController;
    @Mock
    HabitService habitService;
    @Mock
    TagsService tagsService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Test
    @DisplayName("GET /habit/{id} returns existing DTO when habit is found")
    void shouldReturnExistingDtoWhenHabitIsFound() throws Exception {
        HabitDto expectedHabitDto = new HabitDto();
        expectedHabitDto.setId(HABIT_ID);
        expectedHabitDto.setHabitTranslation(new HabitTranslationDto().setLanguageCode(LOCALE.getLanguage()));

        when(habitService.getByIdAndLanguageCode(HABIT_ID, LOCALE.getLanguage())).thenReturn(expectedHabitDto);

        mockMvc.perform(get(BASE_URL + "/{HABIT_ID}", HABIT_ID)
                        .header("Accept-Language", LOCALE.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habitTranslation.languageCode").value(LOCALE.getLanguage()))
                .andExpect(jsonPath("$.id").value(HABIT_ID));

        verify(habitService).getByIdAndLanguageCode(HABIT_ID, LOCALE.getLanguage());
    }

    @Test
    @DisplayName("GET /habit/{id} returns '400 - Bad Request' when passed wrong @id")
    void shouldReturn400WhenPassedWrongId() throws Exception {
        String habitIdStr = "abc";

        mockMvc.perform(get(BASE_URL + "/{HABIT_ID}", habitIdStr)
                        .header("Accept-Language", LOCALE.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(habitService);
    }

    @Test
    @DisplayName("GET /habit returns all default and custom habits")
    void shouldReturnPageableDtoOfHabitDto() throws Exception {
        List<HabitDto> habits = List.of(new HabitDto().setImage(IMG_EXAMPLE));
        PageableDto<HabitDto> page = new PageableDto<>(habits, 1, 0, 1);

        when(habitService.getAllHabitsByLanguageCode(any(UserVO.class), any(Pageable.class), eq(LOCALE.getLanguage()))).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .header("Accept-Language", LOCALE.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("@.page[0].image").value(IMG_EXAMPLE));

        verify(habitService).getAllHabitsByLanguageCode(any(UserVO.class), any(Pageable.class), eq(LOCALE.getLanguage()));
    }

    @Test
    @DisplayName("GET /habit/{id}/shopping-list should return array of 'ShoppingListItemDto'")
    void shouldReturnShoppingListItemDto() throws Exception {
        ShoppingListItemDto dto1 = new ShoppingListItemDto();
        dto1.setId(10L);
        dto1.setText("Reusable bag");

        ShoppingListItemDto dto2 = new ShoppingListItemDto();
        dto2.setId(11L);
        dto2.setText("Glass bottle");

        when(habitService.getShoppingListForHabit(HABIT_ID, LOCALE.getLanguage()))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get(BASE_URL + "/{HABIT_ID}/shopping-list", HABIT_ID)
                        .header("Accept-Language", LOCALE.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].text").value("Reusable bag"))
                .andExpect(jsonPath("$[1].id").value(11L))
                .andExpect(jsonPath("$[1].text").value("Glass bottle"));

        verify(habitService).getShoppingListForHabit(HABIT_ID, LOCALE.getLanguage());
    }

    @Test
    @DisplayName("GET /habit/tags/search should return PageableDto")
    void shouldReturnPageableDtoByTags() throws Exception {
        List<String> tags = List.of("ECO_NEWS", "EVENT");

        HabitDto dto1 = new HabitDto();
        dto1.setId(1L);
        dto1.setTags(List.of("EVENT"));
        dto1.setImage(IMG_EXAMPLE);

        HabitDto dto2 = new HabitDto();
        dto2.setId(2L);
        dto2.setTags(List.of("ECO_NEWS"));
        dto2.setImage(IMG_EXAMPLE);

        PageableDto<HabitDto> pageableDto = new PageableDto<>(List.of(dto1, dto2), 1, 1, 2);

        when(habitService.getAllByTagsAndLanguageCode(any(Pageable.class), eq(tags), eq(LOCALE.getLanguage()))).thenReturn(pageableDto);

        mockMvc.perform(get(BASE_URL + "/tags/search")
                        .param("tags", "ECO_NEWS", "EVENT")
                        .header("Accept-Language", LOCALE.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("@.page[0].id").value(1L))
                .andExpect(jsonPath("@.page[0].image").value(IMG_EXAMPLE))
                .andExpect(jsonPath("@.page[0].tags").value("EVENT"));

        verify(habitService).getAllByTagsAndLanguageCode(any(Pageable.class), eq(tags), eq(LOCALE.getLanguage()));
    }

    @Test
    @DisplayName("GET /habit/search – Filter habits by tags, isCustomHabit, complexities")
    void shouldReturnPageWithSearchResult() throws Exception {
        List<String> tags = List.of("EVENT");
        Boolean isCustom = false;
        List<Integer> complexities = List.of(1);

        HabitDto dto1 = new HabitDto();
        dto1.setId(1L);
        dto1.setTags(List.of("EVENT"));
        dto1.setImage(IMG_EXAMPLE);

        HabitDto dto2 = new HabitDto();
        dto2.setId(2L);
        dto2.setTags(List.of("ECO_NEWS"));
        dto2.setImage(IMG_EXAMPLE);

        PageableDto<HabitDto> pageableDto = new PageableDto<>(List.of(dto1, dto2), 2, 0, 1);

        when(habitService.getAllByDifferentParameters(
                any(UserVO.class),
                any(Pageable.class),
                eq(Optional.of(tags)),
                eq(Optional.of(isCustom)),
                eq(Optional.of(complexities)),
                eq(LOCALE.getLanguage()))).thenReturn(pageableDto);

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("tags", "EVENT")
                        .param("isCustomHabit", "false")
                        .param("complexities", "1")
                        .header("Accept-Language", LOCALE.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("@.page[0].id").value(1L))
                .andExpect(jsonPath("@.page[0].image").value(IMG_EXAMPLE))
                .andExpect(jsonPath("@.page[0].tags").value("EVENT"))
                .andExpect(jsonPath("@.page[1].id").value(2L))
                .andExpect(jsonPath("@.page[1].image").value(IMG_EXAMPLE))
                .andExpect(jsonPath("@.page[1].tags").value("ECO_NEWS"));

        verify(habitService).getAllByDifferentParameters(any(UserVO.class), any(Pageable.class), eq(Optional.of(tags)),
                eq(Optional.of(isCustom)), eq(Optional.of(complexities)), eq(LOCALE.getLanguage()));
    }

    @Test
    @DisplayName("GET /habit/tags – should return all habit tags")
    void shouldReturnAllHabitTags() throws Exception {
        List<String> list = List.of("ECO_NEWS", "EVENT");

        when(tagsService.findAllHabitsTags(LOCALE.getLanguage())).thenReturn(list);

        mockMvc.perform(get(BASE_URL + "/tags")
                        .header("Accept-Language", LOCALE.getLanguage())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ECO_NEWS"))
                .andExpect(jsonPath("$[1]").value("EVENT"));

        verify(tagsService).findAllHabitsTags(LOCALE.getLanguage());
    }

    @Test
    @DisplayName("POST /habit/custom – should add new custom habit with multipart image")
    void shouldAddNewCustomHabitWithMultipartImage() throws Exception {
        AddCustomHabitDtoRequest requestDto = new AddCustomHabitDtoRequest();
        requestDto.setTagIds(Set.of(1L, 2L));
        requestDto.setComplexity(1);
        requestDto.setHabitTranslations(List.of(new HabitTranslationDto()));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MockMultipartFile jsonPart = new MockMultipartFile("request", "", "application/json", jsonRequest.getBytes());

        MockMultipartFile imagePart = new MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image-bytes".getBytes());

        AddCustomHabitDtoResponse responseDto = new AddCustomHabitDtoResponse();
        responseDto.setId(1L);
        responseDto.setImage(IMG_EXAMPLE);

        when(habitService.addCustomHabit(any(), any(), eq("usergreencity@gmail.com"))).thenReturn(responseDto);

        mockMvc.perform(multipart(BASE_URL + "/custom")
                        .file(jsonPart)
                        .file(imagePart)
                        .with(csrf())
                        .principal(() -> "usergreencity@gmail.com")
                        .header("Accept-Language", LOCALE.getLanguage())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.image").value(IMG_EXAMPLE));

        verify(habitService).addCustomHabit(any(), any(), eq("usergreencity@gmail.com"));
    }

    @Test
    @DisplayName("GET /habit/{HABIT_ID}/friends/profile-pictures – should return profile pictures of friends assigned to habit")
    void shouldReturnProfilePicturesOfFriendsAssignedToHabit() throws Exception {
        List<UserProfilePictureDto> userProfilePictureDtos = List.of(
                UserProfilePictureDto.builder()
                        .id(1L)
                        .name("Greg")
                        .profilePicturePath(IMG_EXAMPLE)
                        .build(),
                UserProfilePictureDto.builder()
                        .id(2L)
                        .name("Dora")
                        .profilePicturePath(IMG_EXAMPLE)
                        .build());

        when(habitService.getFriendsAssignedToHabitProfilePictures(eq(HABIT_ID), any())).thenReturn(userProfilePictureDtos);

        mockMvc.perform(get(BASE_URL + "/{HABIT_ID}/friends/profile-pictures", HABIT_ID)
                        .header("Accept-Language", LOCALE.getLanguage())
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Greg"))
                .andExpect(jsonPath("$[0].profilePicturePath").value(IMG_EXAMPLE))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Dora"))
                .andExpect(jsonPath("$[1].profilePicturePath").value(IMG_EXAMPLE));

        verify(habitService).getFriendsAssignedToHabitProfilePictures(eq(HABIT_ID), any());
    }
}
