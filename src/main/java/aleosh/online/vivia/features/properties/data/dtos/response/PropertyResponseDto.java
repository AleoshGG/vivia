package aleosh.online.vivia.features.properties.data.dtos.response;

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
    private String address;
    private String city;
    private String state;
    private String neighborhood;
    private String departmentType;
    private Double area;
    private int roomsNumber;
    private int bathroomsNumber;
    private int parkingNumber;
    private UUID lessorId;
    private List<String> imageUrls;
}
