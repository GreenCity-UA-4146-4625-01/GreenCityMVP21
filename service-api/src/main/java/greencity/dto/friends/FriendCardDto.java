package greencity.dto.friends;

import lombok.*;

/**
 * DTO representing a user's friend card in the search results.
 *
 * Contains basic information about a user that can be shown
 * in the UI when displaying potential friends.
 * @author Mykyta Sirobaba
 */

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class FriendCardDto {
    private Long id;
    private String avatarUrl;
    private String name;
    private Double personalRate;
    private String city;
    private Long mutualFriendsCount;
    private Boolean isRequestSent;
}
