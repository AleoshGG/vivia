package aleosh.online.vivia.features.properties.properties.data.dtos.response;

import java.util.UUID;

public class PropertyDetailPropertyTypeDto {

    private final UUID id;
    private final String name;

    public PropertyDetailPropertyTypeDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
