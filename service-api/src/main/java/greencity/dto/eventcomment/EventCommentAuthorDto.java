package greencity.dto.eventcomment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class EventCommentAuthorDto {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotEmpty
    private String userProfilePicturePath;
}
