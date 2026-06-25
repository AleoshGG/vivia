package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyMediaResponseDto {
    private UUID id;
    private String url;
    private String type;
    private String classification;
}
