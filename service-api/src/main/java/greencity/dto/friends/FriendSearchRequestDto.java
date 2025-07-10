package greencity.dto.friends;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for searching users to add as friends.
 *
 * Contains optional search parameters such as query (name or username),
 * city, and a flag to include friends of friends in the search results.
 *
 * The query must be 1–30 characters long and consist of letters, spaces, or dots only.
 * @author Mykyta Sirobaba
 */

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class FriendSearchRequestDto {
    @Size(min = 1, max = 30, message = "Search query must be 1 to 30 characters")
    @Pattern(regexp = "^[a-zA-Z.\\s]*$", message = "Only letters, dot and space allowed")
    private String query;

    private String city;

    private boolean friendsOfFriends;

}
