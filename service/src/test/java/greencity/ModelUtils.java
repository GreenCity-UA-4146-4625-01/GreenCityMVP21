package greencity;

import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsDtoManagement;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.econews.UpdateEcoNewsDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.event.CreateEventRequestDto;
import greencity.dto.event.EventDateTimeDto;
import greencity.dto.event.EventImageDto;
import greencity.dto.event.EventLocationDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.habit.HabitAssignPropertiesDto;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitfact.HabitFactTranslationVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemResponseDto;
import greencity.dto.shoppinglistitem.CustomShoppingListItemVO;
import greencity.dto.tag.TagDto;
import greencity.dto.tag.TagPostDto;
import greencity.dto.tag.TagTranslationDto;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.tag.TagViewDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.UserFilterDtoRequest;
import greencity.dto.user.UserFilterDtoResponse;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserShoppingListItemResponseDto;
import greencity.dto.user.UserShoppingListItemVO;
import greencity.dto.user.UserStatusDto;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.CustomShoppingListItem;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.Event;
import greencity.entity.EventDateTime;
import greencity.entity.EventImage;
import greencity.entity.EventLocation;
import greencity.entity.Filter;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitFactTranslation;
import greencity.entity.HabitStatistic;
import greencity.entity.HabitStatusCalendar;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import greencity.entity.ShoppingListItem;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.UserShoppingListItem;
import greencity.entity.VerifyEmail;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.CommentStatus;
import greencity.enums.EmailNotification;
import greencity.enums.EventType;
import greencity.enums.EventVisibility;
import greencity.enums.FactOfDayStatus;
import greencity.enums.HabitAssignStatus;
import greencity.enums.HabitRate;
import greencity.enums.Role;
import greencity.enums.ShoppingListItemStatus;
import greencity.enums.TagType;
import greencity.enums.UserStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static greencity.enums.UserStatus.ACTIVATED;

public class ModelUtils {
    public static User TEST_USER = createUser();
    public static User TEST_USER_ROLE_USER = createUserRoleUser();
    public static UserVO TEST_USER_VO = createUserVO();
    public static UserVO TEST_USER_VO_ROLE_USER = createUserVORoleUser();
    public static UserStatusDto TEST_USER_STATUS_DTO = createUserStatusDto();
    public static String TEST_EMAIL = "test@mail.com";
    public static String TEST_EMAIL_2 = "test2@mail.com";
    public static ZonedDateTime zonedDateTime = ZonedDateTime.now();
    public static LocalDateTime localDateTime = LocalDateTime.now();

    public static Tag getTag() {
        return new Tag(1L, TagType.ECO_NEWS, getTagTranslations(), Collections.emptyList(), Collections.emptySet());
    }

    public static Tag getHabitTag() {
        return new Tag(1L, TagType.HABIT, getHabitTagTranslations(), Collections.emptyList(),
                Collections.emptySet());
    }

    public static List<TagTranslation> getTagTranslations() {
        return Arrays.asList(
                TagTranslation.builder().id(1L).name("Новини").language(Language.builder().id(2L).code("ua").build())
                        .build(),
                TagTranslation.builder().id(2L).name("News").language(Language.builder().id(1L).code("en").build())
                        .build());
    }

    public static List<TagTranslation> getHabitTagTranslations() {
        return Arrays.asList(
                TagTranslation.builder().id(1L).name("Багаторазове використання")
                        .language(Language.builder().id(2L).code("ua").build())
                        .build(),
                TagTranslation.builder().id(2L).name("Reusable").language(Language.builder().id(1L).code("en").build())
                        .build());
    }

    public static List<TagTranslation> getEventTagTranslations() {
        Language language = getLanguage();
        return Arrays.asList(
                TagTranslation.builder().id(1L).name("Соціальний").language(getLanguageUa()).build(),
                TagTranslation.builder().id(2L).name("Social").language(language).build(),
                TagTranslation.builder().id(3L).name("Соціальний").language(language).build());
    }

    public static TagDto getTagDto() {
        return TagDto.builder().id(2L).name("News").build();
    }

    public static List<Tag> getTags() {
        return Collections.singletonList(getTag());
    }

    public static List<Tag> getHabitsTags() {
        return Collections.singletonList(getHabitTag());
    }

    public static User getUser() {
        return User.builder()
                .id(1L)
                .email(TestConst.EMAIL)
                .name(TestConst.NAME)
                .role(Role.ROLE_USER)
                .userStatus(UserStatus.ACTIVATED)
                .lastActivityTime(localDateTime)
                .verifyEmail(new VerifyEmail())
                .dateOfRegistration(localDateTime)
                .build();
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
                .id(1L)
                .email(TestConst.EMAIL)
                .name(TestConst.NAME)
                .role(Role.ROLE_USER)
                .lastActivityTime(localDateTime)
                .verifyEmail(new VerifyEmailVO())
                .dateOfRegistration(localDateTime)
                .build();
    }

    public static UserManagementVO getUserManagementVO() {
        return UserManagementVO.builder()
                .id(1L)
                .userStatus(ACTIVATED)
                .email("Test@gmail.com")
                .role(Role.ROLE_ADMIN).build();
    }

    public static UserVO getUserVOWithData() {
        return UserVO.builder()
                .id(13L)
                .name("user")
                .email("namesurname1995@gmail.com")
                .role(Role.ROLE_USER)
                .userCredo("save the world")
                .firstName("name")
                .emailNotification(EmailNotification.MONTHLY)
                .userStatus(UserStatus.ACTIVATED)
                .rating(13.4)
                .verifyEmail(VerifyEmailVO.builder()
                        .id(32L)
                        .user(UserVO.builder()
                                .id(13L)
                                .name("user")
                                .build())
                        .expiryDate(LocalDateTime.of(2021, 7, 7, 7, 7))
                        .token("toooookkkeeeeen42324532542")
                        .build())
                .userFriends(Collections.singletonList(
                        UserVO.builder()
                                .id(75L)
                                .name("Andrew")
                                .build()))
                .refreshTokenKey("refreshtoooookkkeeeeen42324532542")
                .ownSecurity(null)
                .dateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47))
                .city("Lviv")
                .showShoppingList(true)
                .showEcoPlace(true)
                .showLocation(true)
                .ownSecurity(OwnSecurityVO.builder()
                        .id(1L)
                        .password("password")
                        .user(UserVO.builder()
                                .id(13L)
                                .build())
                        .build())
                .lastActivityTime(LocalDateTime.of(2020, 12, 11, 13, 30))
                .build();
    }

    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList());
    }

    public static Language getLanguageUa() {
        return new Language(2L, "ua", Collections.emptyList(), Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        Tag tag = new Tag();
        tag.setTagTranslations(
                List.of(TagTranslation.builder().name("Новини").language(Language.builder().code("ua").build()).build(),
                        TagTranslation.builder().name("News").language(Language.builder().code("en").build()).build()));
        return new EcoNews(1L, zonedDateTime, TestConst.SITE, "source", "shortInfo", getUser(),
                "title", "text", List.of(EcoNewsComment.builder().id(1L).text("test").build()),
                Collections.singletonList(tag), Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNews getEcoNewsForFindDtoByIdAndLanguage() {
        return new EcoNews(1L, null, TestConst.SITE, null, "shortInfo", getUser(),
                "title", "text", null, Collections.singletonList(getTag()), Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNewsVO getEcoNewsVO() {
        return new EcoNewsVO(1L, zonedDateTime, TestConst.SITE, null, getUserVO(),
                "title", "text", null, Collections.emptySet(), Collections.singletonList(getTagVO()),
                Collections.emptySet());
    }

    public static HabitStatusCalendar getHabitStatusCalendar() {
        return HabitStatusCalendar.builder()
                .enrollDate(LocalDate.now()).id(1L).build();
    }

    public static HabitAssign getHabitAssign() {
        return HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .createDate(ZonedDateTime.now())
                .habit(Habit.builder()
                        .id(1L)
                        .image("")
                        .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                                .id(1L)
                                .name("")
                                .description("")
                                .habitItem("")
                                .language(getLanguage())
                                .build()))
                        .build())
                .user(getUser())
                .userShoppingListItems(new ArrayList<>())
                .workingDays(0)
                .duration(0)
                .habitStreak(0)
                .habitStatistic(Collections.singletonList(getHabitStatistic()))
                .habitStatusCalendars(Collections.singletonList(getHabitStatusCalendar()))
                .lastEnrollmentDate(ZonedDateTime.now())
                .build();
    }

    public static HabitStatistic getHabitStatistic() {
        return HabitStatistic.builder()
                .id(1L).habitRate(HabitRate.GOOD).createDate(ZonedDateTime.now())
                .amountOfItems(10).build();
    }

    public static UserShoppingListItem getCustomUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(1L)
                .habitAssign(HabitAssign.builder().id(1L).build())
                .status(ShoppingListItemStatus.DONE)
                .build();
    }

    public static UserShoppingListItem getFullUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(1L)
                .shoppingListItem(getShoppingListItem())
                .habitAssign(HabitAssign.builder().id(1L).build())
                .status(ShoppingListItemStatus.DONE)
                .build();
    }

    public static UserShoppingListItemResponseDto getUserShoppingListItemResponseDto() {
        return UserShoppingListItemResponseDto.builder()
                .id(1L)
                .text("Buy electric car")
                .status(ShoppingListItemStatus.ACTIVE)
                .build();
    }

    public static UserShoppingListItem getPredefinedUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(2L)
                .habitAssign(HabitAssign.builder().id(1L).build())
                .status(ShoppingListItemStatus.ACTIVE)
                .shoppingListItem(ShoppingListItem.builder().id(1L).userShoppingListItems(Collections.emptyList())
                        .translations(
                                getShoppingListItemTranslations())
                        .build())
                .build();
    }

    public static UserShoppingListItemVO getUserShoppingListItemVO() {
        return UserShoppingListItemVO.builder()
                .id(1L)
                .habitAssign(HabitAssignVO.builder()
                        .id(1L)
                        .build())
                .status(ShoppingListItemStatus.DONE)
                .build();
    }

    public static UserShoppingListItem getUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(1L)
                .status(ShoppingListItemStatus.DONE)
                .habitAssign(HabitAssign.builder()
                        .id(1L)
                        .status(HabitAssignStatus.ACQUIRED)
                        .habitStreak(10)
                        .duration(300)
                        .lastEnrollmentDate(ZonedDateTime.now())
                        .workingDays(5)
                        .build())
                .shoppingListItem(ShoppingListItem.builder()
                        .id(1L)
                        .build())
                .dateCompleted(LocalDateTime.of(2021, 2, 2, 14, 2))
                .build();
    }

    public static List<ShoppingListItemTranslation> getShoppingListItemTranslations() {
        return Arrays.asList(
                ShoppingListItemTranslation.builder()
                        .id(2L)
                        .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                                Collections.emptyList()))
                        .content("Buy a bamboo toothbrush")
                        .shoppingListItem(
                                new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                        .build(),
                ShoppingListItemTranslation.builder()
                        .id(11L)
                        .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                                Collections.emptyList()))
                        .content("Start recycling batteries")
                        .shoppingListItem(
                                new ShoppingListItem(4L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                        .build());
    }

    public static HabitFactTranslation getFactTranslation() {
        return HabitFactTranslation.builder()
                .id(1L)
                .factOfDayStatus(FactOfDayStatus.CURRENT)
                .habitFact(null)
                .content("Content")
                .language(getLanguage())
                .build();
    }

    public static HabitFactTranslationVO getFactTranslationVO() {
        return HabitFactTranslationVO.builder()
                .id(1L)
                .factOfDayStatus(FactOfDayStatus.CURRENT)
                .habitFact(null)
                .language(getLanguageVO())
                .content("Content")
                .build();
    }

    public static LanguageTranslationDTO getLanguageTranslationDTO() {
        return new LanguageTranslationDTO(getLanguageDTO(), "content");
    }

    public static LanguageDTO getLanguageDTO() {
        return new LanguageDTO(1L, "en");
    }

    public static AddEcoNewsDtoRequest getAddEcoNewsDtoRequest() {
        return new AddEcoNewsDtoRequest("title", "text",
                Collections.singletonList("News"), "source", null, "shortInfo");
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
                "text", "shortInfo", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
                ZonedDateTime.now(), TestConst.SITE, "source",
                Arrays.asList("Новини", "News"));
    }

    public static MultipartFile getFile() {
        Path path = Paths.get("src/test/resources/test.jpg");
        String name = TestConst.IMG_NAME;
        String contentType = "photo/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MockMultipartFile(name,
                name, contentType, content);
    }

    public static URL getUrl() throws MalformedURLException {
        return new URL(TestConst.SITE);
    }

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return new EcoNewsAuthorDto(1L, TestConst.NAME);
    }

    public static List<TagTranslationVO> getTagTranslationsVO() {
        return Arrays.asList(TagTranslationVO.builder().id(1L).name("Новини")
                        .languageVO(LanguageVO.builder().id(1L).code("ua").build()).build(),
                TagTranslationVO.builder().id(2L).name("News").languageVO(LanguageVO.builder().id(2L).code("en").build())
                        .build());
    }

    public static LanguageVO getLanguageVO() {
        return new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE);
    }

    public static TagVO getTagVO() {
        return new TagVO(1L, TagType.ECO_NEWS, getTagTranslationsVO(), null, null);
    }

    public static TagPostDto getTagPostDto() {
        return new TagPostDto(TagType.ECO_NEWS, getTagTranslationDtos());
    }

    public static List<TagTranslationDto> getTagTranslationDtos() {
        return Arrays.asList(
                TagTranslationDto.TagTranslationDtoBuilder().name("Новини")
                        .language(LanguageDTO.builder().id(2L).code("ua").build()).build(),
                TagTranslationDto.TagTranslationDtoBuilder().name("News")
                        .language(LanguageDTO.builder().id(1L).code("en").build()).build());
    }

    public static TagViewDto getTagViewDto() {
        return new TagViewDto("3", "ECO_NEWS", "News");
    }

    public static PageableAdvancedDto<TagVO> getPageableAdvancedDtoForTag() {
        return new PageableAdvancedDto<>(Collections.singletonList(getTagVO()),
                9, 1, 2, 1,
                true, false, false, true);
    }

    public static AddEcoNewsCommentDtoResponse getAddEcoNewsCommentDtoResponse() {
        return AddEcoNewsCommentDtoResponse.builder()
                .id(getEcoNewsComment().getId())
                .author(getEcoNewsCommentAuthorDto())
                .text(getEcoNewsComment().getText())
                .modifiedDate(getEcoNewsComment().getModifiedDate())
                .build();
    }

    public static EcoNewsComment getEcoNewsComment() {
        return EcoNewsComment.builder()
                .id(1L)
                .text("text")
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .user(getUser())
                .ecoNews(getEcoNews())
                .build();
    }

    public static EcoNewsCommentAuthorDto getEcoNewsCommentAuthorDto() {
        return EcoNewsCommentAuthorDto.builder()
                .id(getUser().getId())
                .name(getUser().getName().trim())
                .userProfilePicturePath(getUser().getProfilePicturePath())
                .build();
    }

    public static AddEcoNewsCommentDtoRequest getAddEcoNewsCommentDtoRequest() {
        return new AddEcoNewsCommentDtoRequest("text", 0L);
    }

    public static EcoNewsCommentDto getEcoNewsCommentDto() {
        return EcoNewsCommentDto.builder()
                .id(1L)
                .modifiedDate(LocalDateTime.now())
                .author(getEcoNewsCommentAuthorDto())
                .text("text")
                .replies(0)
                .likes(0)
                .currentUserLiked(false)
                .status(CommentStatus.ORIGINAL)
                .build();
    }

    public static List<LanguageTranslationDTO> getLanguageTranslationsDTOs() {
        return Arrays.asList(
                new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "hello"),
                new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "text"),
                new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "smile"));
    }

    public static EcoNewsDto getEcoNewsDto() {
        return new EcoNewsDto(ZonedDateTime.now(), "imagePath", 1L, "title", "content", "text",
                getEcoNewsAuthorDto(), Collections.singletonList("tag"), Collections.singletonList("тег"), 1, 0, 0);
    }

    public static EcoNewsGenericDto getEcoNewsGenericDto() {
        String[] tagsEn = {"News"};
        String[] tagsUa = {"Новини"};
        return new EcoNewsGenericDto(1L, "title", "text", "shortInfo",
                ModelUtils.getEcoNewsAuthorDto(), zonedDateTime, "https://google.com/", "source",
                List.of(tagsUa), List.of(tagsEn), 0, 1, 0);
    }

    public static EcoNewsDto getEcoNewsDtoForFindDtoByIdAndLanguage() {
        return new EcoNewsDto(null, TestConst.SITE, 1L, "title", "text", "shortInfo",
                getEcoNewsAuthorDto(), Collections.singletonList("News"), Collections.singletonList("Новини"), 0, 0, 0);
    }

    public static UpdateEcoNewsDto getUpdateEcoNewsDto() {
        return new UpdateEcoNewsDto("1L", "title", "text", Collections.singletonList("tag"),
                "source", "abc");
    }

    public static SearchNewsDto getSearchNewsDto() {
        return new SearchNewsDto(1L, "title", getEcoNewsAuthorDto(), ZonedDateTime.now(),
                Collections.singletonList("tag"));
    }

    public static EcoNewsCommentVO getEcoNewsCommentVO() {
        return new EcoNewsCommentVO(1L, "text", LocalDateTime.now(), LocalDateTime.now(), new EcoNewsCommentVO(),
                new ArrayList<>(), getUserVO(), getEcoNewsVO(), false,
                false, new HashSet<>());
    }

    public static EcoNewsDtoManagement getEcoNewsDtoManagement() {
        return new EcoNewsDtoManagement(1L, "title", "text", ZonedDateTime.now(),
                Collections.singletonList("tag"), "imagePath", "source");
    }

    public static EcoNewsViewDto getEcoNewsViewDto() {
        return new EcoNewsViewDto("1", "title", "author", "text", "startDate",
                "endDate", "tag");
    }

    public static ShoppingListItem getShoppingListItem() {
        return ShoppingListItem.builder()
                .id(1L)
                .translations(getShoppingListItemTranslations())
                .build();
    }

    public static HabitAssignPropertiesDto getHabitAssignPropertiesDto() {
        return HabitAssignPropertiesDto.builder()
                .defaultShoppingListItems(List.of(1L))
                .duration(20)
                .build();
    }

    public static HabitAssign getHabitAssignWithUserShoppingListItem() {
        return HabitAssign.builder()
                .id(1L)
                .user(User.builder().id(21L).build())
                .habit(Habit.builder().id(1L).build())
                .status(HabitAssignStatus.INPROGRESS)
                .workingDays(0)
                .duration(20)
                .userShoppingListItems(List.of(UserShoppingListItem.builder()
                        .id(1L)
                        .shoppingListItem(ShoppingListItem.builder().id(1L).build())
                        .status(ShoppingListItemStatus.INPROGRESS)
                        .build()))
                .build();
    }

    private static UserStatusDto createUserStatusDto() {
        return UserStatusDto.builder()
                .id(2L)
                .userStatus(UserStatus.CREATED)
                .build();
    }

    private static User createUserRoleUser() {
        return User.builder()
                .id(2L)
                .role(Role.ROLE_USER)
                .email("test2@mail.com")
                .build();
    }

    private static UserVO createUserVORoleUser() {
        return UserVO.builder()
                .id(2L)
                .role(Role.ROLE_USER)
                .email("test2@mail.com")
                .build();
    }

    private static User createUser() {
        return User.builder()
                .id(1L)
                .role(Role.ROLE_MODERATOR)
                .email("test@mail.com")
                .build();
    }

    private static UserVO createUserVO() {
        return UserVO.builder()
                .id(1L)
                .role(Role.ROLE_MODERATOR)
                .email("test@mail.com")
                .build();
    }

    public static List<UserShoppingListItemVO> getUserShoppingListItemVOList() {
        List<UserShoppingListItemVO> list = new ArrayList<>();
        list.add(UserShoppingListItemVO.builder()
                .id(1L)
                .build());
        return list;
    }

    public static List<CustomShoppingListItemVO> getCustomShoppingListItemVOList() {
        List<CustomShoppingListItemVO> list = new ArrayList<>();
        list.add(CustomShoppingListItemVO.builder()
                .id(1L)
                .text("text")
                .build());
        return list;
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDto() {
        return CustomShoppingListItemResponseDto.builder()
                .id(1L)
                .status(ShoppingListItemStatus.INPROGRESS)
                .text("TEXT")
                .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItem() {
        return CustomShoppingListItem.builder()
                .id(1L)
                .status(ShoppingListItemStatus.INPROGRESS)
                .text("TEXT")
                .build();
    }

    public static Principal getPrincipal() {
        return () -> "danylo@gmail.com";
    }

    public static UserFilterDtoRequest getUserFilterDtoRequest() {
        return UserFilterDtoRequest.builder()
                .userRole("USER")
                .name("Test_Filter")
                .searchCriteria("Test")
                .userStatus("ACTIVATED")
                .build();
    }

    public static UserFilterDtoResponse getUserFilterDtoResponse() {
        return UserFilterDtoResponse.builder()
                .id(1L)
                .userRole("ADMIN")
                .searchCriteria("Test")
                .userStatus("ACTIVATED")
                .name("Test")
                .build();
    }

    public static Filter getFilter() {
        return Filter.builder()
                .id(1L)
                .name("Test")
                .user(new User())
                .type("USERS")
                .values("Test;ADMIN;ACTIVATED")
                .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItemWithStatusInProgress() {
        return CustomShoppingListItem.builder()
                .id(2L)
                .habit(Habit.builder()
                        .id(3L)
                        .build())
                .user(getUser())
                .text("item")
                .status(ShoppingListItemStatus.INPROGRESS)
                .build();
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDtoWithStatusInProgress() {
        return CustomShoppingListItemResponseDto.builder()
                .id(2L)
                .text("item")
                .status(ShoppingListItemStatus.INPROGRESS)
                .build();
    }

    public static EventResponseDto getEventResponseDto() {
        return EventResponseDto.builder()
                .eventId(1L)
                .title("Test")
                .description("Join us for a week-long festival celebrating sustainability, urban gardening, and eco-friendly living. Expect workshops, talks, and fun activities suitable for all ages.")
                .visibility(EventVisibility.OPEN)
                .eventTypes(Set.of(EventType.PLACE, EventType.ONLINE))
                .eventDateTimes(List.of(
                        EventDateTimeDto.builder()
                                .date(LocalDate.of(2025, 8, 1))
                                .startTime(LocalTime.of(10, 0))
                                .endTime(LocalTime.of(18, 0))
                                .allDay(false)
                                .build(),
                        EventDateTimeDto.builder()
                                .date(LocalDate.of(2025, 8, 2))
                                .startTime(LocalTime.of(10, 0))
                                .endTime(LocalTime.of(18, 0))
                                .allDay(false)
                                .build()
                ))
                .locations(List.of(
                        EventLocationDto.builder()
                                .address("123 Eco Street, Green City Park")
                                .latitude(50.4501)
                                .longitude(30.5234)
                                .build()
                ))
                .onlineLinks(List.of("https://greencityfestival.online"))
                .images(List.of(
                        EventImageDto.builder()
                                .imageId(1L)
                                .isMain(true)
                                .build(),
                        EventImageDto.builder()
                                .imageId(2L)
                                .isMain(false)
                                .build()
                ))
                .mainImageId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static CreateEventRequestDto getCreateEventRequestDto() {
        return CreateEventRequestDto.builder()
                .title("Test")
                .description("Join us for a week-long festival celebrating sustainability, urban gardening, and eco-friendly living. Expect workshops, talks, and fun activities suitable for all ages.")
                .visibility(EventVisibility.OPEN)
                .eventTypes(Set.of(EventType.PLACE, EventType.ONLINE))
                .locations(List.of(
                        EventLocationDto.builder()
                                .address("123 Eco Street, Green City Park")
                                .latitude(50.4501)
                                .longitude(30.5234)
                                .build()
                ))
                .onlineLinks(List.of("https://greencityfestival.online"))
                .eventDateTimes(List.of(
                        EventDateTimeDto.builder()
                                .date(LocalDate.of(2025, 8, 1))
                                .startTime(LocalTime.of(10, 0))
                                .endTime(LocalTime.of(18, 0))
                                .allDay(false)
                                .build(),
                        EventDateTimeDto.builder()
                                .date(LocalDate.of(2025, 8, 2))
                                .startTime(LocalTime.of(9, 30))
                                .endTime(LocalTime.of(17, 45))
                                .allDay(false)
                                .build()
                ))
                .mainImageId(1L)
                .build();

    }

    public static Event createEvent() {
        return Event.builder()
                .id(1L)
                .title("Event 1")
                .description("A global conference on sustainability, innovation, and future technologies for greener cities.")
                .mainImageId(1001L)
                .creationDate(LocalDateTime.of(2025, 7, 1, 12, 0))
                .lastUpdateDate(LocalDateTime.of(2025, 7, 5, 14, 30))
                .eventVisibility(EventVisibility.OPEN)
                .eventTypes(Set.of(EventType.PLACE, EventType.ONLINE))
                .eventLocations(List.of(
                        EventLocation.builder()
                                .id(1L)
                                .address("123 Sustainability Blvd, EcoCity")
                                .latitude(50.4501)
                                .longitude(30.5234)
                                .event(null)
                                .build()
                ))
                .onlineLinks(List.of(
                        "https://greenconference.org/live",
                        "https://zoom.us/eco-event-2025"
                ))
                .eventImages(List.of(
                        EventImage.builder()
                                .id(1L)
                                .url("https://cdn.greencity.com/events/image1.jpg")
                                .isMain(true)
                                .event(null)
                                .build(),
                        EventImage.builder()
                                .id(2L)
                                .url("https://cdn.greencity.com/events/image2.jpg")
                                .isMain(false)
                                .event(null)
                                .build()
                ))
                .eventDateTimes(List.of(
                        EventDateTime.builder()
                                .id(1L)
                                .date(LocalDate.of(2025, 8, 15))
                                .startTime(LocalTime.of(10, 0))
                                .endTime(LocalTime.of(18, 0))
                                .allDay(false)
                                .event(null)
                                .build(),
                        EventDateTime.builder()
                                .id(2L)
                                .date(LocalDate.of(2025, 8, 16))
                                .startTime(LocalTime.of(11, 0))
                                .endTime(LocalTime.of(17, 0))
                                .allDay(false)
                                .event(null)
                                .build()
                ))
                .creator(User.builder()
                        .id(42L)
                        .name("Eco Organizer")
                        .email("organizer@greencity.com")
                        .build())
                .build();

    }
}
