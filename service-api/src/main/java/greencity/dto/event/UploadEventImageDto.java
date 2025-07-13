package greencity.dto.event;

import greencity.annotations.ValidImage;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadEventImageDto {
    @NotNull
    @ValidImage
    private MultipartFile image;

    @NotNull
    private Boolean isMainImage;
}