package aleosh.online.vivia.features.properties.likes.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToggleLikeResponseDto {
    private boolean liked;
}
