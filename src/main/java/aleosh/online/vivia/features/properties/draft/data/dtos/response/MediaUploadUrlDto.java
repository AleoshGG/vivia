package aleosh.online.vivia.features.properties.draft.data.dtos.response;

public class MediaUploadUrlDto {

    private final String fileKey;
    private final String uploadUrl;
    private final String storageKey;
    private final int expiresIn;

    public MediaUploadUrlDto(String fileKey, String uploadUrl, String storageKey, int expiresIn) {
        this.fileKey = fileKey;
        this.uploadUrl = uploadUrl;
        this.storageKey = storageKey;
        this.expiresIn = expiresIn;
    }

    public String getFileKey() { return fileKey; }
    public String getUploadUrl() { return uploadUrl; }
    public String getStorageKey() { return storageKey; }
    public int getExpiresIn() { return expiresIn; }
}
