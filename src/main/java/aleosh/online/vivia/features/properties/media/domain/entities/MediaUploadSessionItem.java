package aleosh.online.vivia.features.properties.media.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaUploadSessionItem {

    private final String fileKey;
    private final String contentType;
    private final long sizeBytes;
    private final String classification;
    private final String storageKey;

    @JsonCreator
    public MediaUploadSessionItem(
            @JsonProperty("fileKey") String fileKey,
            @JsonProperty("contentType") String contentType,
            @JsonProperty("sizeBytes") long sizeBytes,
            @JsonProperty("classification") String classification,
            @JsonProperty("storageKey") String storageKey
    ) {
        this.fileKey = fileKey;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.classification = classification;
        this.storageKey = storageKey;
    }

    public String getFileKey() { return fileKey; }
    public String getContentType() { return contentType; }
    public long getSizeBytes() { return sizeBytes; }
    public String getClassification() { return classification; }
    public String getStorageKey() { return storageKey; }
}
