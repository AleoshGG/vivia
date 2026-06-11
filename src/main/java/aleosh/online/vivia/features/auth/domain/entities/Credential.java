package aleosh.online.vivia.features.auth.domain.entities;

import aleosh.online.vivia.features.auth.domain.objectvalues.CredentialType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Credential {
    private final UUID id;
    private final UUID userId;
    private final CredentialType credentialType;
    private final String providerCredentialId;
    private final String secretData;

    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public Credential(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.credentialType = builder.credentialType;
        this.providerCredentialId = builder.providerCredentialId;
        this.secretData = builder.secretData;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }


    // Geters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public CredentialType getCredentialType() { return credentialType; }
    public String getProviderCredentialId() { return providerCredentialId; }
    public  String getSecretData() { return secretData; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public static Builder build() {return new Builder();}

    public static class Builder {
        private UUID id;
        private UUID userId;
        private CredentialType credentialType;
        private String providerCredentialId;
        private String secretData;

        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder credentialType(CredentialType type) { this.credentialType = type; return this; }
        public Builder providerCredentialType(String providerCredentialId) { this.providerCredentialId = providerCredentialId; return this; }
        public Builder secretData(String secretData) { this.secretData = secretData; return this; }

        public Builder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Credential build() { return new Credential(this); }

    }

}
