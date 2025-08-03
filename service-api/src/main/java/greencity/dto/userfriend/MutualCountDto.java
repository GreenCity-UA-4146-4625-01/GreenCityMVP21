package greencity.dto.userfriend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class MutualCountDto {
    private Long targetUserId;
    private Long count;
}
