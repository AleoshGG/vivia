package aleosh.online.vivia.features.properties.properties.domain.entities;

import aleosh.online.vivia.features.properties.properties.domain.exceptions.InvalidPropertyException;

import java.util.UUID;

public class PropertyMedia {

    public enum MediaType {
        IMAGE,
        VIDEO
    }

    private final UUID id;
    private final String url;
    private final MediaType type;
    private final String classification;

    private PropertyMedia(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.url = builder.url;
        this.type = builder.type;
        this.classification = builder.classification;
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidPropertyException("PropertyMedia ID is required");
        }
        if (builder.url == null || builder.url.isBlank()) {
            throw new InvalidPropertyException("PropertyMedia URL is required");
        }
        if (builder.type == null) {
            throw new InvalidPropertyException("PropertyMedia type is required");
        }
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public String getUrl() { return url; }
    public MediaType getType() { return type; }
    public String getClassification() { return classification; }

    public static class Builder {
        private UUID id;
        private String url;
        private MediaType type;
        private String classification;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder url(String url) { this.url = url; return this; }
        public Builder type(MediaType type) { this.type = type; return this; }
        public Builder classification(String classification) { this.classification = classification; return this; }

        public PropertyMedia build() { return new PropertyMedia(this); }
    }
}
