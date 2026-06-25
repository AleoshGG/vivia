package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import java.util.UUID;

public class PropertyDetailAddressDto {

    private final UUID id;
    private final String street;
    private final String exteriorNumber;
    private final String interiorNumber;
    private final PropertyDetailNeighborhoodDto neighborhood;

    public PropertyDetailAddressDto(UUID id, String street, String exteriorNumber,
                                    String interiorNumber, PropertyDetailNeighborhoodDto neighborhood) {
        this.id = id;
        this.street = street;
        this.exteriorNumber = exteriorNumber;
        this.interiorNumber = interiorNumber;
        this.neighborhood = neighborhood;
    }

    public UUID getId() { return id; }
    public String getStreet() { return street; }
    public String getExteriorNumber() { return exteriorNumber; }
    public String getInteriorNumber() { return interiorNumber; }
    public PropertyDetailNeighborhoodDto getNeighborhood() { return neighborhood; }
}
