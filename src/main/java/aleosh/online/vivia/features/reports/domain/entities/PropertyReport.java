package aleosh.online.vivia.features.reports.domain.entities;

import aleosh.online.vivia.features.reports.domain.exceptions.ReportAlreadyExistsException;

import java.time.Instant;
import java.util.UUID;

public class PropertyReport {

    private final UUID id;
    private final UUID propertyId;
    private final String propertyTitle;
    private final UUID lessorId;
    private final UUID lesseeId;
    private final String reasonId;
    private final String comment;
    private final boolean isResolved;
    private final String verdict;
    private final UUID resolvedBy;
    private final Instant createdAt;
    private final Instant resolvedAt;

    private PropertyReport(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.propertyId = builder.propertyId;
        this.propertyTitle = builder.propertyTitle;
        this.lessorId = builder.lessorId;
        this.lesseeId = builder.lesseeId;
        this.reasonId = builder.reasonId;
        this.comment = builder.comment;
        this.isResolved = builder.isResolved;
        this.verdict = builder.verdict;
        this.resolvedBy = builder.resolvedBy;
        this.createdAt = builder.createdAt;
        this.resolvedAt = builder.resolvedAt;
    }

    private void validate(Builder builder) {
        if (builder.id == null)
            throw new ReportAlreadyExistsException("Report ID is required");
        if (builder.propertyTitle == null || builder.propertyTitle.isBlank())
            throw new ReportAlreadyExistsException("Property title is required");
        if (builder.lessorId == null)
            throw new ReportAlreadyExistsException("Lessor ID is required");
        if (builder.lesseeId == null)
            throw new ReportAlreadyExistsException("Lessee ID is required");
        if (builder.reasonId == null || builder.reasonId.isBlank())
            throw new ReportAlreadyExistsException("Reason ID is required");
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public UUID getPropertyId() { return propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public UUID getLessorId() { return lessorId; }
    public UUID getLesseeId() { return lesseeId; }
    public String getReasonId() { return reasonId; }
    public String getComment() { return comment; }
    public boolean isResolved() { return isResolved; }
    public String getVerdict() { return verdict; }
    public UUID getResolvedBy() { return resolvedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }

    public static class Builder {
        private UUID id;
        private UUID propertyId;
        private String propertyTitle;
        private UUID lessorId;
        private UUID lesseeId;
        private String reasonId;
        private String comment;
        private boolean isResolved = false;
        private String verdict;
        private UUID resolvedBy;
        private Instant createdAt;
        private Instant resolvedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder propertyId(UUID propertyId) { this.propertyId = propertyId; return this; }
        public Builder propertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; return this; }
        public Builder lessorId(UUID lessorId) { this.lessorId = lessorId; return this; }
        public Builder lesseeId(UUID lesseeId) { this.lesseeId = lesseeId; return this; }
        public Builder reasonId(String reasonId) { this.reasonId = reasonId; return this; }
        public Builder comment(String comment) { this.comment = comment; return this; }
        public Builder isResolved(boolean isResolved) { this.isResolved = isResolved; return this; }
        public Builder verdict(String verdict) { this.verdict = verdict; return this; }
        public Builder resolvedBy(UUID resolvedBy) { this.resolvedBy = resolvedBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder resolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; return this; }

        public PropertyReport build() { return new PropertyReport(this); }
    }
}
