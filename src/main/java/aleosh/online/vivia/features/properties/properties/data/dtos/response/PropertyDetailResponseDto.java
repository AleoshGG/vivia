package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import aleosh.online.vivia.features.users.lessor.data.dtos.response.LessorResponseDto;
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
public class PropertyDetailResponseDto {
    private UUID id;
    private String title;
    private String description;
    private Double price;
    private String departmentType;
    private Double area;
    private int roomsNumber;
    private int bathroomsNumber;
    private int parkingNumber;
    private LessorResponseDto lessor;
    private List<String> imageUrls;
}
