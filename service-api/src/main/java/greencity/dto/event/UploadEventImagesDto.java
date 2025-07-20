package greencity.dto.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadEventImagesDto {
    @Valid
    @Size(max = 5, message = "Maximum 5 images allowed")
    private List<UploadEventImageDto> images;
}