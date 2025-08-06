package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.user.UserVO;
import greencity.dto.userfriend.UserCardDto;
import greencity.enums.FriendStatus;
import greencity.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    /**
     * Searches for users to add as friends based on query.
     *
     * @param query          Search string (e.g., name or email).
     * @param city           If true, filters users by current user's city.
     * @param mutualFriends  If true, prioritizes users with mutual friends.
     * @param page           Pagination info.
     * @param userVO         The current authenticated user performing the search.
     * @return A paginated list of users matching the criteria.
     */
    @Operation(summary = "Search a new friend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/search")
    public ResponseEntity<PageableDto<UserCardDto>> searchFriends(@RequestParam @Size(min = 1, max = 30) String query,
                                                                  @RequestParam(required = false) Boolean city,
                                                                  @RequestParam(required = false) Boolean mutualFriends,
                                                                  @Parameter(hidden = true) Pageable page,
                                                                  @CurrentUser UserVO userVO) {
        return ResponseEntity.ok(friendService.searchUsers(query, city, mutualFriends, page, userVO.getId()));
    }

    /**
     * Sends a friend request to the specified user.
     *
     * @param friendId ID of the user to send a friend request to.
     * @param userVO   The current authenticated user.
     * @return HTTP 200 if the request was sent successfully.
     */
    @Operation(summary = "Add new user friend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<Void> addNewFriend(
            @Parameter(description = "Id friend of current user. Cannot be empty.") @PathVariable long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        friendService.sendFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Accepts a previously received friend request from the specified user.
     *
     * @param friendId ID of the user who sent the request.
     * @param userVO   The current authenticated user accepting the request.
     * @return HTTP 200 if the request was accepted successfully.
     */
    @Operation(summary = "Accept friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @PatchMapping("/{friendId}/acceptFriend")
    public ResponseEntity<Void> acceptFriendRequest(
            @Parameter(description = "Friend's id. Cannot be empty.") @PathVariable long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        friendService.acceptFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * Cancels or rejects a friend request received from the specified user.
     *
     * @param friendId ID of the user whose friend request is to be cancelled.
     * @param userVO   The current authenticated user rejecting the request.
     * @return HTTP 200 if the request was successfully cancelled.
     */
    @Operation(summary = "Cancel friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @DeleteMapping("/cancel/{friendId}")
    public ResponseEntity<Void> cancelFriendRequest(
            @Parameter(description = "Id of user whose friend request will be cancelled.") @PathVariable long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        friendService.cancelFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Handles HTTP POST request to revoke a previously sent friend request.
     * <p>
     * This method cancels the friend request sent by the current authenticated user
     * to the user identified by the given friendId.
     * Upon successful execution, it returns HTTP status 200 OK with no response body.
     *
     * @param friendId the ID of the user whose friend request will be revoked
     * @param userVO   the currently authenticated user (injected automatically)
     * @return ResponseEntity with HTTP status 200 OK if the request was successfully revoked
     */
    @Operation(summary = "Revoke a sent friend request",
            description = "Cancels the friend request sent by the current user to the specified friend.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
                    content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @PostMapping("/{friendId}/revoke")
    public ResponseEntity<Void> revokeFriendRequest(
            @Parameter(description = "User ID to which the request was sent", required = true)
            @PathVariable long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        friendService.revokeFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Retrieves all friends of the current user.
     *
     * @param userVO The current authenticated user.
     * @param page   Pagination info.
     * @return A paginated list of current user's friends.
     */
    @Operation(summary = "Get all friends of current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
    })
    @GetMapping
    public ResponseEntity<PageableDto<UserCardDto>> getAllFriends(
            @Parameter(hidden = true) @CurrentUser UserVO userVO, @Parameter(hidden = true) Pageable page) {
        PageableDto<UserCardDto> friends = friendService.getAllFriends(userVO.getId(), page);
        return ResponseEntity.ok(friends);
    }

    /**
     * Retrieves the current friend status between the authenticated user and the specified user.
     *
     * @param friendId ID of the user to check the friend status with.
     * @param userVO   The current authenticated user.
     * @return HTTP 200 with the friendship status if found; HTTP 404 otherwise.
     */
    @Operation(summary = "Get friendship status with specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @GetMapping("/{friendId}/status")
    public ResponseEntity<FriendStatus> getFriendStatus(
            @Parameter(description = "Friend user ID to check status with.") @PathVariable long friendId,
            @Parameter(hidden = true) @CurrentUser UserVO userVO) {
        return friendService.getFriendStatus(userVO.getId(), friendId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete friendship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
            @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
            @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
                    content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
            @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
                    content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND))),
    })
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> unfriend(@PathVariable Long friendId,
                                         @Parameter(hidden = true) @CurrentUser UserVO user) {
        friendService.unfriend(user.getId(), friendId);
        return ResponseEntity.noContent().build();
    }
}
