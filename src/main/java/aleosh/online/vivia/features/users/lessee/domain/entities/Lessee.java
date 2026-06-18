package aleosh.online.vivia.features.users.lessee.domain.entities;

import aleosh.online.vivia.features.users.lessee.domain.exceptions.InvalidLesseeException;
import java.util.UUID;

public class Lessee {
    private final UUID id;
    private final Double latitude;
    private final Double longitude;

    private Lessee(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    private void validate(Builder builder) {
        if (builder.id == null) 
            throw new InvalidLesseeException("Lessee ID is required");

        if (builder.latitude != null && (builder.latitude < -90 || builder.latitude > 90))
            throw new InvalidLesseeException("Latitude must be between -90 and 90");

        if (builder.longitude != null && (builder.longitude < -180 || builder.longitude > 180))
            throw new InvalidLesseeException("Longitude must be between -180 and 180");
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }

    public static class Builder {
        private UUID id;
        private Double latitude;
        private Double longitude;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
        public Builder longitude(Double longitude) { this.longitude = longitude; return this; }

        public Lessee build() { return new Lessee(this); }
    }
}
