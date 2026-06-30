package aleosh.online.vivia.features.users.lessor.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.exceptions.InvalidLessorDocumentException;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.DocumentType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class LessorDocument {
    private final UUID id;
    private final UUID lessorId;
    private final DocumentType documentType;
    private final String uri;
    private final OffsetDateTime uploadedAt;

    private LessorDocument(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.lessorId = builder.lessorId;
        this.documentType = builder.documentType;
        this.uri = builder.uri;
        this.uploadedAt = builder.uploadedAt != null ? builder.uploadedAt : OffsetDateTime.now();
    }

    private void validate(Builder builder) {
        if (builder.id == null)
            throw new InvalidLessorDocumentException("Document ID is required");
        if (builder.lessorId == null)
            throw new InvalidLessorDocumentException("Lessor ID is required");
        if (builder.documentType == null)
            throw new InvalidLessorDocumentException("Document type is required");
        if (builder.uri == null || builder.uri.isBlank())
            throw new InvalidLessorDocumentException("Document URI is required");
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public UUID getLessorId() { return lessorId; }
    public DocumentType getDocumentType() { return documentType; }
    public String getUri() { return uri; }
    public OffsetDateTime getUploadedAt() { return uploadedAt; }

    public static class Builder {
        private UUID id;
        private UUID lessorId;
        private DocumentType documentType;
        private String uri;
        private OffsetDateTime uploadedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder lessorId(UUID lessorId) { this.lessorId = lessorId; return this; }
        public Builder documentType(DocumentType documentType) { this.documentType = documentType; return this; }
        public Builder uri(String uri) { this.uri = uri; return this; }
        public Builder uploadedAt(OffsetDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public LessorDocument build() { return new LessorDocument(this); }
    }
}
