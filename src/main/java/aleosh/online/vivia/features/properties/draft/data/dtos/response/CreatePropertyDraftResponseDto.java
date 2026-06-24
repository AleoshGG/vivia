package aleosh.online.vivia.features.properties.draft.data.dtos.response;

import aleosh.online.vivia.features.properties.draft.data.dtos.response.MediaUploadUrlDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyDraftResponseDto {

    private UUID draftId;
    private String status;
    private String expiresAt;
    private List<MediaUploadUrlDto> uploads;
}
