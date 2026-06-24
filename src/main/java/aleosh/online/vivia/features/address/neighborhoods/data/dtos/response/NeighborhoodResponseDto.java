package aleosh.online.vivia.features.address.neighborhoods.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeighborhoodResponseDto {
    private UUID id;
    private String name;
    private String postalCode;
}
