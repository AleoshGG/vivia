package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaManifestItemDto {

    @NotBlank(message = "File key is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "File key must contain only alphanumeric characters, hyphens, and underscores")
    private String fileKey;

    @NotBlank(message = "Content type is required")
    private String contentType;

    @Positive(message = "Size must be greater than zero")
    private Long sizeBytes;

    private String classification;
}
