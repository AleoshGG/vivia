package aleosh.online.vivia.features.properties.media.domain.entities;

import aleosh.online.vivia.features.properties.media.domain.exceptions.InvalidMediaOperationException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonDeserialize(builder = MediaUploadSession.Builder.class)
public class MediaUploadSession {

    private final UUID id;
    private final UUID propertyId;
    private final UUID lessorId;
    private final Map<String, MediaUploadSessionItem> mediaFiles;
    private final String status;
    private final int totalFiles;
    private final Instant createdAt;
    private final Instant expiresAt;

    private MediaUploadSession(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.propertyId = builder.propertyId;
        this.lessorId = builder.lessorId;
        this.mediaFiles = builder.mediaFiles;
        this.status = builder.status;
        this.totalFiles = builder.totalFiles;
        this.createdAt = builder.createdAt;
        this.expiresAt = builder.expiresAt;
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidMediaOperationException("MediaUploadSession ID is required");
        }
        if (builder.propertyId == null) {
            throw new InvalidMediaOperationException("Property ID is required");
        }
        if (builder.lessorId == null) {
            throw new InvalidMediaOperationException("Lessor ID is required");
        }
        if (builder.mediaFiles == null || builder.mediaFiles.isEmpty()) {
            throw new InvalidMediaOperationException("At least one media file is required");
        }
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public UUID getPropertyId() { return propertyId; }
    public UUID getLessorId() { return lessorId; }
    public Map<String, MediaUploadSessionItem> getMediaFiles() { return mediaFiles; }
    public String getStatus() { return status; }
    public int getTotalFiles() { return totalFiles; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private UUID id;
        private UUID propertyId;
        private UUID lessorId;
        private Map<String, MediaUploadSessionItem> mediaFiles;
        private String status;
        private int totalFiles;
        private Instant createdAt;
        private Instant expiresAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder propertyId(UUID propertyId) { this.propertyId = propertyId; return this; }
        public Builder lessorId(UUID lessorId) { this.lessorId = lessorId; return this; }
        public Builder mediaFiles(Map<String, MediaUploadSessionItem> mediaFiles) { this.mediaFiles = mediaFiles; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder totalFiles(int totalFiles) { this.totalFiles = totalFiles; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }

        public MediaUploadSession build() { return new MediaUploadSession(this); }
    }
}
