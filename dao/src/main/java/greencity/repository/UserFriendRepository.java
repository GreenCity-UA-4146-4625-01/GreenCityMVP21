package greencity.repository;

import greencity.dto.userfriend.MutualCountDto;
import greencity.entity.User;
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
        ON CONFLICT (user_id, friend_id) DO UPDATE SET status = EXCLUDED.status
        """, nativeQuery = true)
    void addOrUpdateFriendRequest(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") String status);

    /**
     * Deletes a specific friend request between two users by status.
     *
     * @param userId   ID of the user who received the request
     * @param friendId ID of the user who sent the request
     * @param status   Friendship status to match (e.g., REQUEST)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM UserFriend uf WHERE uf.user.id = :friendId AND uf.friend.id = :userId AND uf.status = :status")
    void deleteFriendRequest(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") FriendStatus status);

    /**
     * Finds all friends of a user by given friendship status with pagination.
     *
     * @param userId ID of the user whose friends are being queried
     * @param status Desired friendship status (e.g., FRIEND)
     * @param pageable Pagination info
     * @return Paginated list of user friends
     */
    @Query("SELECT uf.friend FROM UserFriend uf WHERE uf.user.id = :userId AND uf.status = :status")
    Page<User> findFriendsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") FriendStatus status, Pageable pageable);

    /**
     * Finds the number of mutual friends between the current user and a list of other users.
     *
     * @param currentUserId ID of the current user
     * @param targetUserIds List of user IDs to compare with
     * @param status Friendship status to consider (usually FRIEND)
     * @return List of DTOs with user ID and mutual friend count
     */
    @Query("""
    SELECT new greencity.dto.userfriend.MutualCountDto(uf2.user.id, COUNT(*))
    FROM UserFriend uf1
    JOIN UserFriend uf2 ON uf1.friend.id = uf2.friend.id
    WHERE uf1.user.id = :currentUserId
      AND uf2.user.id IN :targetUserIds
      AND uf1.status = :status
      AND uf2.status = :status
    GROUP BY uf2.user.id
""")
    List<MutualCountDto> findMutualCounts(@Param("currentUserId") Long currentUserId,
                                          @Param("targetUserIds") List<Long> targetUserIds,
                                          @Param("status") FriendStatus status);

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
     * Counts the number of mutual friends between two users.
     *
     * @param userId       ID of the first user
     * @param targetUserId ID of the second user
     * @param status       Friendship status to consider
     * @return Number of mutual friends
     */
    @Query("""
    SELECT COUNT(*)
    FROM UserFriend uf1
    JOIN UserFriend uf2 ON uf1.friend.id = uf2.friend.id
    WHERE uf1.user.id = :userId
      AND uf2.user.id = :targetUserId
      AND uf1.status = :status
      AND uf2.status = :status
""")
    Long countMutualFriends(@Param("userId") Long userId,
                            @Param("targetUserId") Long targetUserId,
                            @Param("status") FriendStatus status);

    /**
     * Searches for users by name or email, with optional filters:
     * - by same city as current user
     * - by mutual friends
     * - excluding already added friends
     * - excluding the current user
     *
     * @param currentUserId ID of the user performing the search
     * @param query Search term (part of name or email)
     * @param sameCity If true, only returns users from the same city
     * @param mutual If true, returns only users with mutual friends
     * @return List of users matching search criteria
     */
    @Query("""
    SELECT DISTINCT u
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
        AND (:mutual = FALSE OR u.id IN (
            SELECT ff.friend.id FROM UserFriend f
            JOIN UserFriend ff ON f.friend.id = ff.user.id
            WHERE f.user.id = :currentUserId
              AND ff.status = 'FRIEND'
              AND ff.friend.id NOT IN (
                  SELECT fr.friend.id FROM UserFriend fr
                  WHERE fr.user.id = :currentUserId AND fr.status = 'FRIEND'
              )
        ))
    ORDER BY u.name
""")
    List<User> searchUsers(
            @Param("currentUserId") Long currentUserId,
            @Param("query") String query,
            @Param("sameCity") boolean sameCity,
            @Param("mutual") boolean mutual
    );
}

