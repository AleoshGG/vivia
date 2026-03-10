package aleosh.online.vivia.features.users.lessee.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.valueobjects.PasskeyCredential;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lessee {

    private final UUID id;
    private final byte[] userHandle;
    private final String username;
    private final String email;
    private final List<PasskeyCredential> credentials;

    public Lessee(Builder builder) {
        this.id = builder.id;
        this.userHandle = builder.userHandle;
        this.username = builder.username;
        this.email = builder.email;
        this.credentials = builder.credentials != null ?
                new ArrayList<>(builder.credentials) :
                new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public byte[] getUserHandle() { return userHandle; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public List<PasskeyCredential> getCredentials() {
        return Collections.unmodifiableList(credentials);
    }

    public static class Builder {
        private UUID id;
        private byte[] userHandle;
        private String username;
        private String email;
        private List<PasskeyCredential> credentials = new ArrayList<>();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder userHandle(byte[] userHandle) { this.userHandle = userHandle; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }

        public Builder credentials(List<PasskeyCredential> credentials) {
            this.credentials = credentials; return this;
        }

        public Builder addCredential(PasskeyCredential credential) {
            this.credentials.add(credential); return this;
        }

        public Lessee build() { return new Lessee(this); }
    }

}