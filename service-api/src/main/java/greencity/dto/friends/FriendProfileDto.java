package greencity.dto.friends;

import lombok.*;

/**
 * DTO representing detailed profile information of a potential or existing friend.
 *
 * Used to display full friend profile data, including friendship status.
 * @author Mykyta Sirobaba
 */

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class FriendProfileDto {
    private Long id;

    private String avatarUrl;

    private String name;

    private String username;

    private String city;

    private Double personalRate;

    private Long mutualFriendsCount;

    private Boolean isRequestSent;

    private Boolean isAlreadyFriend;
}
