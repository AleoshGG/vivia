package aleosh.online.vivia.features.properties.domain.entities;

import java.util.UUID;

public class PropertyImage {
    private final UUID id;
    private final String url;

    private PropertyImage(Builder builder) {
        this.id = builder.id;
        this.url = builder.url;
    }

    public UUID getId() { return id; }
    public String getUrl() { return url; }

    public static class Builder {
        private UUID id;
        private String url;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder url(String url) { this.url = url; return this; }

        public PropertyImage build() {
            return new PropertyImage(this);
        }
    }
}