package aleosh.online.vivia.features.properties.draft.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class PropertyDraftMedia {

    private final UUID id;
    private final UUID draftId;
    private final String fileKey;
    private final String contentType;
    private final String storageKey;
    private final String status;
    private final String classification;

    @JsonCreator
    public PropertyDraftMedia(
            @JsonProperty("id") UUID id,
            @JsonProperty("draftId") UUID draftId,
            @JsonProperty("fileKey") String fileKey,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("storageKey") String storageKey,
            @JsonProperty("status") String status,
            @JsonProperty("classification") String classification
    ) {
        this.id = id;
        this.draftId = draftId;
        this.fileKey = fileKey;
        this.contentType = contentType;
        this.storageKey = storageKey;
        this.status = status;
        this.classification = classification;
    }

    public UUID getId() { return id; }
    public UUID getDraftId() { return draftId; }
    public String getFileKey() { return fileKey; }
    public String getContentType() { return contentType; }
    public String getStorageKey() { return storageKey; }
    public String getStatus() { return status; }
    public String getClassification() { return classification; }
}
