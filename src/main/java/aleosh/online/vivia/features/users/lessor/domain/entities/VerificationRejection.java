package aleosh.online.vivia.features.users.lessor.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.exceptions.InvalidVerificationException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class VerificationRejection {
    private final UUID id;
    private final UUID lessorId;
    private final String comment;
    private final List<String> reasons;
    private final OffsetDateTime createdAt;

    private VerificationRejection(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.lessorId = builder.lessorId;
        this.comment = builder.comment;
        this.reasons = builder.reasons;
        this.createdAt = builder.createdAt != null ? builder.createdAt : OffsetDateTime.now();
    }

    private void validate(Builder builder) {
        if (builder.id == null) {
            throw new InvalidVerificationException("Rejection ID is required");
        }
        if (builder.lessorId == null) {
            throw new InvalidVerificationException("Lessor ID is required");
        }
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public UUID getLessorId() { return lessorId; }
    public String getComment() { return comment; }
    public List<String> getReasons() { return reasons; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public static class Builder {
        private UUID id;
        private UUID lessorId;
        private String comment;
        private List<String> reasons;
        private OffsetDateTime createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder lessorId(UUID lessorId) { this.lessorId = lessorId; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }
        public Builder reasons(List<String> reasons) { this.reasons = reasons; return this; }
        public Builder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }

        public VerificationRejection build() { return new VerificationRejection(this); }
    }
}
