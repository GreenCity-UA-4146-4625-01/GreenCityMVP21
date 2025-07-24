package greencity.dto.eventcomment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventShortInfoUserVO {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotEmpty
    private String userProfilePicturePath;
}
