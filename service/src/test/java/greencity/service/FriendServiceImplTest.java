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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DisplayName("FriendServiceImpl unit tests")
class FriendServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserFriendRepository userFriendRepository;

    @InjectMocks
    private FriendServiceImpl friendService;

    private UserCardDto userCardDto;

    @BeforeEach
    void setUp() {
        User user = new User();
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
    @DisplayName("Should return filtered UserCardDto list with mutual friends count")
    void searchUsers_withMutualAndCityFilter() {
        Pageable pageable = PageRequest.of(0, 10);

        List<UserCardDto> userDtos = List.of(userCardDto);
        Page<UserCardDto> usersPage = new PageImpl<>(userDtos, pageable, 1);

        when(userFriendRepository.searchUsers(1L, "Jo", true, true, pageable))
                .thenReturn(usersPage);

        PageableDto<UserCardDto> result = friendService.searchUsers("Jo", true, true, pageable, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getPage()).hasSize(1);
        assertThat(result.getPage().getFirst().getName()).isEqualTo("John");
        assertThat(result.getPage().getFirst().getMutualFriendsCount()).isEqualTo(3L);

        verify(userFriendRepository).searchUsers(1L, "Jo", true, true, pageable);
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
    @DisplayName("Should accept friend request and create bidirectional friendship")
    void acceptFriendRequest_success() {
        Long currentUserId = 123L;
        Long friendId = 124L;

        when(userRepo.existsById(currentUserId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userFriendRepository.existsFriendshipWithStatus(currentUserId, friendId, FriendStatus.FRIEND.toString()))
                .thenReturn(false);
        when(userFriendRepository.isFriendRequestedByFriend(currentUserId, friendId))
                .thenReturn(true);

        doNothing().when(userFriendRepository).acceptFriendRequest(currentUserId, friendId);

        friendService.acceptFriendRequest(currentUserId, friendId);

        verify(userRepo).existsById(currentUserId);
        verify(userRepo).existsById(friendId);
        verify(userFriendRepository).existsFriendshipWithStatus(currentUserId, friendId, FriendStatus.FRIEND.toString());
        verify(userFriendRepository).isFriendRequestedByFriend(currentUserId, friendId);
        verify(userFriendRepository).addOrUpdateFriendRequest(currentUserId, friendId, FriendStatus.FRIEND.toString());
        verify(userFriendRepository).addOrUpdateFriendRequest(friendId, currentUserId, FriendStatus.FRIEND.toString());
    }

    @Test
    @DisplayName("Should return all friends with mutual friends count (pagination simulated)")
    void getAllFriends_success() {
        Pageable pageable = PageRequest.of(0, 5);

        List<UserCardDto> mockFriends = List.of(userCardDto.setFriend(true));

        when(userRepo.existsById(1L)).thenReturn(true);
        when(userFriendRepository.getAllFriends(1L)).thenReturn(mockFriends);

        PageableDto<UserCardDto> result = friendService.getAllFriends(1L, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPage()).hasSize(1);
        assertThat(result.getPage().getFirst().getName()).isEqualTo("John");
        assertThat(result.getPage().getFirst().getMutualFriendsCount()).isEqualTo(3L);
        assertThat(result.getPage().getFirst().isFriend()).isTrue();

        verify(userRepo).existsById(1L);
        verify(userFriendRepository).getAllFriends(1L);
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

    @Test
    @DisplayName("Should successfully revoke sent friend request when all validations pass")
    void revokeFriendRequest_success() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(currentUserId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userFriendRepository.existsFriendshipWithStatus(currentUserId, friendId, "REQUEST")).thenReturn(true);
        when(userFriendRepository.existsFriendshipWithStatus(currentUserId, friendId, "FRIEND")).thenReturn(false);
        when(userFriendRepository.isFriendRequestedByCurrentUser(currentUserId, friendId)).thenReturn(true);

        friendService.revokeFriendRequest(currentUserId, friendId);

        verify(userRepo).existsById(currentUserId);
        verify(userRepo).existsById(friendId);
        verify(userFriendRepository).existsFriendshipWithStatus(currentUserId, friendId, "REQUEST");
        verify(userFriendRepository).existsFriendshipWithStatus(currentUserId, friendId, "FRIEND");
        verify(userFriendRepository).isFriendRequestedByCurrentUser(currentUserId, friendId);
        verify(userFriendRepository).revokeFriendRequest(currentUserId, friendId);
    }

    @Test
    @DisplayName("Should successfully remove friendship")
    void shouldRemoveFriendshipSuccessfully() {
        Long currentUserId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(currentUserId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userFriendRepository.existsFriendshipWithStatus(currentUserId, friendId, "FRIEND")).thenReturn(true);

        friendService.unfriend(currentUserId, friendId);

        verify(userRepo).existsById(currentUserId);
        verify(userRepo).existsById(friendId);
        verify(userFriendRepository).deleteRelationshipByStatus(currentUserId, friendId, FriendStatus.FRIEND);
    }

}
