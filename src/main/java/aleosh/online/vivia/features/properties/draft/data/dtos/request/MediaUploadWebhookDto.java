package aleosh.online.vivia.features.properties.draft.data.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaUploadWebhookDto {

    private String bucket;
    private String key;
    private long size;

    @JsonProperty("eventType")
    private String eventType;

    public String getBucket() { return bucket; }
    public String getKey() { return key; }
    public long getSize() { return size; }
    public String getEventType() { return eventType; }
}
