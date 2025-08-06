package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.userfriend.UserCardDto;
import greencity.enums.FriendStatus;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FriendService {
    PageableDto<UserCardDto> searchUsers(String query, Boolean city, Boolean mutualFriends,
                             Pageable pageable, Long currentUserId);

    void sendFriendRequest(Long currentUserId, Long friendId);

    void cancelFriendRequest(Long currentUserId, Long friendId);

    PageableDto<UserCardDto> getAllFriends(Long currentUserId, Pageable pageable);

    Optional<FriendStatus> getFriendStatus(Long currentUserId, Long userId);

    void acceptFriendRequest(Long currentUserId, Long friendId);

    void revokeFriendRequest(Long currentUserId, Long friendId);

    void unfriend(Long userId, Long friendId);
    }

