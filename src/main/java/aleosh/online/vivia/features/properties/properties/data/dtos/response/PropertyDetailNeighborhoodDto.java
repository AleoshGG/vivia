package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import java.util.UUID;

public class PropertyDetailNeighborhoodDto {

    private final UUID id;
    private final String name;
    private final String postalCode;

    public PropertyDetailNeighborhoodDto(UUID id, String name, String postalCode) {
        this.id = id;
        this.name = name;
        this.postalCode = postalCode;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getPostalCode() { return postalCode; }
}
