package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Double price;
    private aleosh.online.vivia.features.properties.address.data.dtos.AddressDto address;
    private String departmentType;
    private Double area;
    private int roomsNumber;
    private int bathroomsNumber;
    private int parkingNumber;
    private UUID lessorId;
    private List<String> imageUrls;
}
