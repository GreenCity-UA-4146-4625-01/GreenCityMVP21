package greencity.dto.userfriend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class UserCardDto {
    private Long id;
    private String name;
    private String city;
    private Double rating;
    private String profilePicturePath;
    private Long mutualFriendsCount;
    private boolean isFriend;
}
