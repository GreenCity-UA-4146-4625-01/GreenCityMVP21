package greencity.repository;

import greencity.dto.userfriend.UserCardDto;
import greencity.entity.UserFriend;
import greencity.entity.UserFriendId;
import greencity.enums.FriendStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFriendRepository extends JpaRepository<UserFriend, UserFriendId> {

    /**
     * Inserts a new friend request or updates the existing one with a new status.
     * Uses native SQL with UPSERT via `ON CONFLICT`.
     *
     * @param userId   ID of the user who sends the request
     * @param friendId ID of the user who receives the request
     * @param status   Status of the friendship (e.g., REQUEST, FRIEND)
     */
    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO users_friends(user_id, friend_id, status, created_date)
            VALUES (:userId, :friendId, :status, CURRENT_TIMESTAMP)
            ON CONFLICT (user_id, friend_id) DO UPDATE SET status = :status
            """, nativeQuery = true)
    void addOrUpdateFriendRequest(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") String status);

    /**
     * Deletes a specific friend request between two users by status.
     *
     * @param userId   ID of the user who received the request
     * @param friendId ID of the user who sent the request
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserFriend uf WHERE uf.user.id = :friendId AND uf.friend.id = :userId AND uf.status = :status ")
    void deleteRelationshipByStatus(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") FriendStatus status);

    /**
     * Retrieves the current friendship status between two users.
     *
     * @param userId   ID of the first user
     * @param friendId ID of the second user
     * @return Optional containing the friendship status if it exists
     */
    @Query("SELECT uf.status FROM UserFriend uf WHERE uf.user.id = :userId AND uf.friend.id = :friendId")
    Optional<FriendStatus> findStatusByUsers(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * Searches for users by name or email, with optional filters:
     * - by same city as current user
     * - by mutual friends
     * - excluding already added friends
     * - excluding the current user
     *
     * @param currentUserId ID of the user performing the search
     * @param query         Search term (part of name or email)
     * @param sameCity      If true, only returns users from the same city
     * @param mutual        If true, returns only users with mutual friends
     * @return List of {@link UserCardDto}
     */
    @Query("""
    SELECT new greencity.dto.userfriend.UserCardDto(
        u.id,
        u.name,
        u.city,
        u.rating,
        u.profilePicturePath,
        (
            SELECT COUNT(uf2.friend.id) * 1L
            FROM UserFriend uf2
            WHERE uf2.user.id = :currentUserId
              AND uf2.status = 'FRIEND'
              AND uf2.friend.id IN (
                  SELECT uf3.friend.id
                  FROM UserFriend uf3
                  WHERE uf3.user.id = u.id
                    AND uf3.status = 'FRIEND'
              )
        ),
        false\s
    )
    FROM User u
    WHERE (LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))
      AND (:sameCity = FALSE OR u.city = (
          SELECT currentUser.city FROM User currentUser WHERE currentUser.id = :currentUserId
      ))
      AND u.id NOT IN (
          SELECT f.friend.id FROM UserFriend f
          WHERE f.user.id = :currentUserId AND f.status = 'FRIEND'
      )
      AND u.id != :currentUserId
      AND (:mutual = FALSE OR EXISTS (
          SELECT 1
          FROM UserFriend uf1
          JOIN UserFriend uf2 ON uf1.friend.id = uf2.friend.id
          WHERE uf1.user.id = :currentUserId
            AND uf2.user.id = u.id
            AND uf1.status = 'FRIEND'
            AND uf2.status = 'FRIEND'
      ))
""")
    Page<UserCardDto> searchUsers(
            @Param("currentUserId") Long currentUserId,
            @Param("query") String query,
            @Param("sameCity") boolean sameCity,
            @Param("mutual") boolean mutual,
            Pageable pageable
    );


    /**
     * Checks whether two users are already friends, regardless of direction.
     * Friendship is considered mutual if a record with status = 'FRIEND' exists in either direction.
     *
     * @param userId   ID of the first user
     * @param friendId ID of the second user
     * @return true if users are friends, false otherwise
     */
    @Query(nativeQuery = true,
            value = "SELECT EXISTS(SELECT * FROM users_friends WHERE status = :status AND ("
                    + "user_id = :userId AND friend_id = :friendId OR "
                    + "user_id = :friendId AND friend_id = :userId))")
    boolean existsFriendshipWithStatus(Long userId, Long friendId, @Param("status") String status);

    /**
     * Checks whether a friend request was specifically sent from the friend to the current user.
     * This is useful for confirming that the current user is the receiver of the request.
     *
     * @param userId   ID of the current user (request receiver)
     * @param friendId ID of the user who potentially sent the request
     * @return true if a request from friendId to userId exists, false otherwise
     */
    @Query(nativeQuery = true,
            value = "SELECT EXISTS(SELECT * FROM users_friends WHERE status = 'REQUEST' AND "
                    + "user_id = :friendId AND friend_id = :userId) ")
    boolean isFriendRequestedByFriend(Long userId, Long friendId);

    /**
     * Deletes a friend request with status 'REQUEST' from the current user to the specified friend.
     * <p>
     * This method removes the pending friend request record in the database.
     *
     * @param currentUserId the ID of the user who sent the friend request
     * @param friendId      the ID of the user who received the friend request
     */
    @Modifying
    @Query("DELETE FROM UserFriend uf WHERE uf.user.id = :currentUserId AND uf.friend.id = :friendId AND uf.status = 'REQUEST'")
    void revokeFriendRequest(@Param("currentUserId") Long currentUserId, @Param("friendId") Long friendId);

    /**
     * Checks if a friend request with status 'REQUEST' exists from userId to friendId.
     *
     * @param userId   the ID of the user who may have sent the friend request
     * @param friendId the ID of the user who may have received the friend request
     * @return true if such a friend request exists, false otherwise
     */
    @Query(nativeQuery = true,
            value = "SELECT EXISTS(SELECT 1 FROM users_friends WHERE status = 'REQUEST' AND user_id = :userId AND friend_id = :friendId)")
    boolean isFriendRequestedByCurrentUser(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * Accepts a friend request by updating its status to 'FRIEND'.
     * This method assumes the request exists in direction from friend to current user.
     *
     * @param userId   ID of the current user (receiver of request)
     * @param friendId ID of the user who sent the friend request
     */
    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE users_friends SET status = 'FRIEND' "
                    + "WHERE user_id = :friendId AND friend_id = :userId")
    void acceptFriendRequest(Long userId, Long friendId);

    /**
     * Retrieves a list of the current user's friends as UserCardDto objects.
     * Each DTO contains basic information about the friend along with the count of mutual friends
     * between the current user and that friend.
     * <p>
     * The query selects users who are friends of the user with the given currentUserId and have a 'FRIEND' status.
     * For each friend, it calculates the number of mutual friends shared with the current user.
     *
     * @param currentUserId the ID of the user whose friends are being retrieved
     * @return a list of {@link UserCardDto} objects, each containing:
     *         - friend's id
     *         - friend's name
     *         - friend's city
     *         - friend's rating
     *         - friend's profile picture path
     *         - number of mutual friends with the current user (as Long)
     *         - a boolean flag set to true (possibly indicating friend status)
     */
    @Query("""
    SELECT new greencity.dto.userfriend.UserCardDto(
        u.id,
        u.name,
        u.city,
        u.rating,
        u.profilePicturePath,
        (
            SELECT COUNT(uf2.friend.id) * 1L
            FROM UserFriend uf2
            WHERE uf2.user.id = :currentUserId
              AND uf2.status = 'FRIEND'
              AND uf2.friend.id IN (
                  SELECT uf3.friend.id
                  FROM UserFriend uf3
                  WHERE uf3.user.id = u.id
                    AND uf3.status = 'FRIEND'
              )
        ),
        true
    )
    FROM UserFriend uf
    JOIN uf.friend u
    WHERE uf.user.id = :currentUserId
      AND uf.status = 'FRIEND'
""")
    List<UserCardDto> getAllFriends(@Param("currentUserId") Long currentUserId);
}

