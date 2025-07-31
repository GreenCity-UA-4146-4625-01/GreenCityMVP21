package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.userfriend.UserCardDto;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserFriendRepository;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DisplayName("FriendServiceImpl unit tests")
class FriendServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserFriendRepository userFriendRepository;

    @InjectMocks
    private FriendServiceImpl friendService;

    private User user;
    private UserCardDto userCardDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setCity("Kyiv");
        user.setRating(4.0);
        user.setProfilePicturePath("/images/avatar.png");

        userCardDto = UserCardDto.builder()
                .id(1L)
                .name("John")
                .city("Kyiv")
                .rating(4.0)
                .profilePicturePath("/images/avatar.png")
                .mutualFriendsCount(3L)
                .isFriend(false)
                .build();
    }

    @Test
    @DisplayName("Should return filtered users with mutual friends count")
    void searchUsers_withMutualAndCityFilter() {
        List<User> users = List.of(user);
        Pageable pageable = PageRequest.of(0, 10);

        when(userFriendRepository.searchUsers(eq(1L), anyString(), eq(true), eq(true))).thenReturn(users);
        when(modelMapper.map(user, UserCardDto.class)).thenReturn(userCardDto);
        when(userFriendRepository.countMutualFriends(1L, 1L, FriendStatus.FRIEND)).thenReturn(3L);

        PageableDto<UserCardDto> result = friendService.searchUsers("Jo", true, true, pageable, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getPage()).hasSize(1);
        assertThat(result.getPage().getFirst().getName()).isEqualTo("John");

        verify(userFriendRepository).searchUsers(1L, "Jo", true, true);
        verify(userFriendRepository).countMutualFriends(1L, 1L, FriendStatus.FRIEND);
    }

    @Test
    @DisplayName("Should throw exception when user sends friend request to self")
    void sendFriendRequest_toSelf_throwsException() {
        long userId = 1L;

        Throwable thrown = catchThrowable(() ->
                friendService.sendFriendRequest(userId, userId));

        assertThat(thrown)
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorMessage.OWN_USER_ID + userId);
    }

    @Test
    @DisplayName("Should throw NotFoundException when user does not exist")
    void sendFriendRequest_userNotFound() {
        when(userRepo.existsById(1L)).thenReturn(true);
        when(userRepo.existsById(2L)).thenReturn(false);

        Throwable thrown = catchThrowable(() ->
                friendService.sendFriendRequest(1L, 2L));

        assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorMessage.USER_NOT_FOUND_BY_ID + 2L);
    }

    @Test
    @DisplayName("Should accept friend request")
    void acceptFriendRequest_success() {
        when(userRepo.existsById(1L)).thenReturn(true);
        when(userRepo.existsById(2L)).thenReturn(true);
        when(userRepo.isFriend(1L, 2L)).thenReturn(false);
        when(userRepo.isFriendRequestedByCurrentUser(1L, 2L)).thenReturn(true);

        friendService.acceptFriendRequest(1L, 2L);

        verify(userRepo).acceptFriendRequest(1L, 2L);
    }

    @Test
    @DisplayName("Should return all friends with pagination")
    void getAllFriends_success() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepo.existsById(1L)).thenReturn(true);
        when(userFriendRepository.findFriendsByUserIdAndStatus(1L, FriendStatus.FRIEND, pageable)).thenReturn(page);
        when(userFriendRepository.findMutualCounts(eq(1L), anyList(), eq(FriendStatus.FRIEND)))
                .thenReturn(List.of(new greencity.dto.userfriend.MutualCountDto(1L, 3L)));
        when(modelMapper.map(user, UserCardDto.class)).thenReturn(userCardDto);

        PageableDto<UserCardDto> result = friendService.getAllFriends(1L, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getPage()).hasSize(1);
        assertThat(result.getPage().getFirst().getMutualFriendsCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Should return friend status if found")
    void getFriendStatus_success() {
        when(userRepo.existsById(1L)).thenReturn(true);
        when(userRepo.existsById(2L)).thenReturn(true);
        when(userFriendRepository.findStatusByUsers(1L, 2L)).thenReturn(Optional.of(FriendStatus.FRIEND));

        Optional<FriendStatus> result = friendService.getFriendStatus(1L, 2L);

        assertThat(result).contains(FriendStatus.FRIEND);
    }
}
