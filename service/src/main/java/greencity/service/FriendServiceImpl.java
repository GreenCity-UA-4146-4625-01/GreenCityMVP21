package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.userfriend.MutualCountDto;
import greencity.dto.userfriend.UserCardDto;
import greencity.entity.User;
import greencity.enums.FriendStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserFriendRepository;
import greencity.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final ModelMapper modelMapper;
    private final UserRepo userRepository;
    private final UserFriendRepository userFriendRepository;

    /**
     * Searches users by query with optional filters: by city and by mutual friends.
     *
     * @param query          search term for name or email
     * @param filterByCity   if true, filters users by current user's city
     * @param filterByMutual if true, prioritizes users with mutual friends
     * @param pageable       pagination information
     * @param currentUserId  the ID of the user performing the search
     * @return pageable list of user cards matching search criteria
     */
    @Override
    public PageableDto<UserCardDto> searchUsers(String query, Boolean filterByCity,
                                                Boolean filterByMutual, Pageable pageable,
                                                Long currentUserId) {

        List<User> users = userFriendRepository.searchUsers(currentUserId, query, filterByCity, filterByMutual);

        List<UserCardDto> dtoList = users.stream()
                .map(user -> {
                    UserCardDto dto = modelMapper.map(user, UserCardDto.class);
                    Long mutualCount = userFriendRepository.countMutualFriends(currentUserId, user.getId(), FriendStatus.FRIEND);
                    dto.setMutualFriendsCount(mutualCount);
                    dto.setFriend(false);
                    return dto;
                })
                .toList();

        return new PageableDto<>(dtoList, dtoList.size(), pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * Sends a friend request from current user to another user.
     *
     * @param currentUserId sender of the request
     * @param friendId      recipient of the request
     * @throws BadRequestException if request is invalid (already sent, self-request, etc.)
     * @throws NotFoundException   if either user does not exist
     */
    @Override
    @Transactional
    public void sendFriendRequest(Long currentUserId, Long friendId) {
        validateUserAndFriendNotSamePerson(currentUserId, friendId);
        validateUserAndFriendExistence(currentUserId, friendId);
        validateFriendNotExists(currentUserId, friendId);
        validateFriendRequestNotSent(currentUserId, friendId);
        userFriendRepository.addOrUpdateFriendRequest(currentUserId, friendId, FriendStatus.REQUEST.toString());
    }

    /**
     * Accepts an incoming friend request from another user.
     *
     * @param currentUserId the user accepting the request
     * @param friendId      the user who sent the request
     * @throws BadRequestException if already friends or invalid operation
     * @throws NotFoundException   if request does not exist or users not found
     */
    @Override
    @Transactional
    public void acceptFriendRequest(Long currentUserId, Long friendId) {
        validateUserAndFriendNotSamePerson(currentUserId, friendId);
        validateUserAndFriendExistence(currentUserId, friendId);
        validateFriendNotExists(currentUserId, friendId);
        validateFriendRequestSentByFriend(currentUserId, friendId);
        userRepository.acceptFriendRequest(currentUserId, friendId);
    }

    /**
     * Cancels an incoming friend request (decline).
     *
     * @param currentUserId the user cancelling the request
     * @param friendId      the user who sent the request
     * @throws BadRequestException if request is invalid or already friends
     * @throws NotFoundException   if no request found
     */
    @Override
    @Transactional
    public void cancelFriendRequest(Long currentUserId, Long friendId) {
        validateUserAndFriendNotSamePerson(currentUserId, friendId);
        validateUserAndFriendExistence(currentUserId, friendId);
        validateFriendNotExists(currentUserId, friendId);
        validateFriendRequestSentByFriend(currentUserId, friendId);
        userFriendRepository.deleteFriendRequest(currentUserId, friendId, FriendStatus.REQUEST);
    }

    /**
     * Retrieves a paginated list of all friends for the current user.
     *
     * @param currentUserId ID of the user whose friends to retrieve
     * @param pageable      pagination parameters
     * @return pageable list of user cards representing friends
     * @throws NotFoundException if user is not found
     */
    @Override
    public PageableDto<UserCardDto> getAllFriends(Long currentUserId, Pageable pageable) {
        Objects.requireNonNull(pageable);
        validateUserExistence(currentUserId);

        Page<User> friendPage = userFriendRepository.findFriendsByUserIdAndStatus(currentUserId, FriendStatus.FRIEND, pageable);
        List<User> friends = friendPage.getContent();
        if (friends.isEmpty()) {
            return new PageableDto<>(Collections.emptyList(), 0, pageable.getPageNumber(), pageable.getPageSize());
        }

        List<Long> friendIds = friends.stream().map(User::getId).toList();

        List<MutualCountDto> mutualCounts = userFriendRepository.findMutualCounts(currentUserId, friendIds, FriendStatus.FRIEND);
        Map<Long, Long> mutualMap = mutualCounts.stream()
                .collect(Collectors.toMap(MutualCountDto::getTargetUserId, MutualCountDto::getCount));

        Map<Long, Boolean> friendMap = friendIds.stream()
                .collect(Collectors.toMap(id -> id, id -> true));

        List<UserCardDto> dtos = friends.stream()
                .map(user -> {
                    UserCardDto dto = modelMapper.map(user, UserCardDto.class);
                    dto.setMutualFriendsCount(mutualMap.getOrDefault(user.getId(), 0L));
                    dto.setFriend(friendMap.getOrDefault(user.getId(), false));
                    return dto;
                })
                .toList();

        return new PageableDto<>(dtos, (int) friendPage.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
    }

    /**
     * Retrieves current friend status between two users.
     *
     * @param currentUserId first user ID
     * @param friendId      second user ID
     * @return Optional containing the friend status if it exists
     * @throws NotFoundException if one of the users is not found
     */
    @Override
    public Optional<FriendStatus> getFriendStatus(Long currentUserId, Long friendId) {
        validateUserAndFriendExistence(currentUserId, friendId);
        return userFriendRepository.findStatusByUsers(currentUserId, friendId);
    }

    /**
     * Validates that the user is not trying to add themselves as a friend.
     *
     * @param userId   ID of the user
     * @param friendId ID of the potential friend
     * @throws BadRequestException if IDs are equal
     */
    private void validateUserAndFriendNotSamePerson(long userId, long friendId) {
        if (userId == friendId) {
            throw new BadRequestException(ErrorMessage.OWN_USER_ID + friendId);
        }
    }

    /**
     * Validates that both users exist in the system.
     *
     * @param userId   ID of the first user
     * @param friendId ID of the second user
     * @throws NotFoundException if either user is not found
     */
    private void validateUserAndFriendExistence(long userId, long friendId) {
        validateUserExistence(userId);
        validateUserExistence(friendId);
    }

    /**
     * Validates that a user exists in the system.
     *
     * @param userId ID of the user to check
     * @throws NotFoundException if user is not found
     */
    private void validateUserExistence(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }

    /**
     * Validates that a friend request has not already been sent.
     *
     * @param userId   ID of the user sending the request
     * @param friendId ID of the target user
     * @throws BadRequestException if request already sent
     */
    private void validateFriendRequestNotSent(long userId, long friendId) {
        if (userRepository.isFriendRequested(userId, friendId)) {
            throw new BadRequestException(ErrorMessage.FRIEND_REQUEST_ALREADY_SENT);
        }
    }

    /**
     * Validates that the users are not already friends.
     *
     * @param userId   ID of the first user
     * @param friendId ID of the second user
     * @throws BadRequestException if they are already friends
     */
    private void validateFriendNotExists(long userId, long friendId) {
        if (userRepository.isFriend(userId, friendId)) {
            throw new BadRequestException(ErrorMessage.FRIEND_EXISTS + friendId);
        }
    }

    /**
     * Validates that a friend request has been sent by the other user.
     *
     * @param userId   ID of the user accepting/declining the request
     * @param friendId ID of the user who sent the request
     * @throws NotFoundException if request not found
     */
    private void validateFriendRequestSentByFriend(long userId, long friendId) {
        if (!userRepository.isFriendRequestedByCurrentUser(userId, friendId)) {
            throw new NotFoundException(ErrorMessage.FRIEND_REQUEST_NOT_SENT);
        }
    }

}

