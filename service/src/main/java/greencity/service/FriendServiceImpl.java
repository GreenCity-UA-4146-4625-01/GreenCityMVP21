package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.userfriend.UserCardDto;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        Page<UserCardDto> usersPage = userFriendRepository.searchUsers(currentUserId, query, filterByCity, filterByMutual, pageable);
        List<UserCardDto> users = usersPage.getContent();

        return new PageableDto<>(
                users,
                usersPage.getTotalElements(),
                usersPage.getNumber(),
                usersPage.getSize()
        );
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
        ensureBidirectionalFriendship(currentUserId, friendId);
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
        userFriendRepository.deleteRelationshipByStatus(currentUserId, friendId, FriendStatus.REQUEST);
    }

    /**
     * Revokes a previously sent friend request from the current user to the specified friend.
     * The method performs the following validations before revoking the request:
     * Checks that the current user and friend are not the same person.
     * Verifies that both users exist.
     * Confirms that a friend request was actually sent.
     * Ensures the users are not already friends.
     * Validates that the friend request was sent by the current user.
     * If all validations pass, the friend request is revoked in the repository.
     *
     * @param currentUserId the ID of the user who sent the friend request
     * @param friendId      the ID of the user to whom the friend request was sent
     * @throws IllegalArgumentException if any validation fails
     */
    @Override
    @Transactional
    public void revokeFriendRequest(Long currentUserId, Long friendId) {
        validateUserAndFriendNotSamePerson(currentUserId, friendId);
        validateUserAndFriendExistence(currentUserId, friendId);
        validateFriendRequestWasSent(currentUserId, friendId);
        validateFriendNotExists(currentUserId, friendId);
        validateFriendRequestSentByCurrentUser(currentUserId, friendId);
        userFriendRepository.revokeFriendRequest(currentUserId, friendId);
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

        List<UserCardDto> allFriends = userFriendRepository.getAllFriends(currentUserId);

        if (allFriends.isEmpty()) {
            return new PageableDto<>(Collections.emptyList(), 0, pageable.getPageNumber(), pageable.getPageSize());
        }

        return new PageableDto<>(allFriends, allFriends.size(), pageable.getPageNumber(), pageable.getPageSize());
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
        if (userFriendRepository.existsFriendshipWithStatus(userId, friendId, FriendStatus.REQUEST.toString())) {
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
        if (userFriendRepository.existsFriendshipWithStatus(userId, friendId, FriendStatus.FRIEND.toString())) {
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
        if (!userFriendRepository.isFriendRequestedByFriend(userId, friendId)) {
            throw new NotFoundException(ErrorMessage.FRIEND_REQUEST_NOT_SENT);
        }
    }

    /**
     * Ensures that a friendship relationship exists bidirectionally between two users.
     * <p>
     * This method sets the friendship status to "FRIEND" in both directions:
     * from userId1 to userId2 and from userId2 to userId1.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     */
    private void ensureBidirectionalFriendship(Long userId1, Long userId2) {
        userFriendRepository.addOrUpdateFriendRequest(userId1, userId2, "FRIEND");
        userFriendRepository.addOrUpdateFriendRequest(userId2, userId1, "FRIEND");
    }

    /**
     * Validates that a friend request with status "REQUEST" exists from userId to friendId.
     * <p>
     * Throws a BadRequestException if no such friend request is found.
     *
     * @param userId   the ID of the user who supposedly sent the friend request
     * @param friendId the ID of the user who supposedly received the friend request
     * @throws BadRequestException if no friend request with status "REQUEST" exists between the users
     */
    private void validateFriendRequestWasSent(long userId, long friendId) {
        if (!userFriendRepository.existsFriendshipWithStatus(userId, friendId, FriendStatus.REQUEST.toString())) {
            throw new BadRequestException(ErrorMessage.NOT_FOUND_REQUEST + friendId);
        }
    }

    /**
     * Validates that the friend request from userId to friendId was actually sent by the current user.
     * <p>
     * Throws a BadRequestException if the friend request was not sent by the user.
     *
     * @param userId   the ID of the user who is expected to have sent the friend request
     * @param friendId the ID of the recipient user of the friend request
     * @throws BadRequestException if the friend request was not sent by the specified user
     */
    private void validateFriendRequestSentByCurrentUser(long userId, long friendId) {
        boolean sentByUser = userFriendRepository.isFriendRequestedByCurrentUser(userId, friendId);
        if (!sentByUser) {
            throw new BadRequestException(ErrorMessage.FRIEND_REQUEST_NOT_SENT_BY_USER + userId);
        }
    }

    /**
     * Validates that a friendship exists between two users with the status {@link FriendStatus#FRIEND}.
     * <p>
     * If the friendship does not exist, this method throws a {@link NotFoundException}.
     * This check ensures that operations depending on an existing friendship
     * (e.g., removing a friend) cannot proceed when the relationship does not exist.
     * </p>
     *
     * @param userId   the ID of the current user.
     * @param friendId the ID of the friend to check.
     * @throws NotFoundException if no friendship with status {@code FRIEND} exists between the given users.
     */
    private void validateFriendExists(long userId, long friendId) {
        if (!userFriendRepository.existsFriendshipWithStatus(userId, friendId, FriendStatus.FRIEND.toString())) {
            throw new NotFoundException(ErrorMessage.FRIENDSHIP_NOT_EXISTS + friendId);
        }
    }


    /**
     * Removes an existing friendship relationship between two users.
     * <p>
     * This method validates that:
     * <ul>
     *     <li>The user is not trying to unfriend themselves</li>
     *     <li>Both users exist in the system</li>
     * </ul>
     * If all validations pass, the friendship relationship is deleted from both sides
     * (user → friend and friend → user) in the database.
     * </p>
     *
     * <p>The operation is transactional, ensuring that both deletions succeed or both are rolled back
     * in case of failure.</p>
     *
     * @param userId   the ID of the user initiating the unfriend action
     * @param friendId the ID of the user to be removed from the friend list
     */
    @Override
    @Transactional
    public void unfriend(Long userId, Long friendId) {
        validateUserAndFriendNotSamePerson(userId, friendId);
        validateUserAndFriendExistence(userId, friendId);
        validateFriendExists(userId, friendId);

        userFriendRepository.deleteRelationshipByStatus(userId, friendId, FriendStatus.FRIEND);
        userFriendRepository.deleteRelationshipByStatus(friendId, userId, FriendStatus.FRIEND);
    }
}

