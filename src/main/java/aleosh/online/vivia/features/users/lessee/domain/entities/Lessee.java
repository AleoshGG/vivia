package aleosh.online.vivia.features.users.lessee.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.valueobjects.PasskeyCredential;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

public class Lessee {

    private final UUID id;
    private final byte[] userHandle;
    private final String username;
    private final String email;
    private final String fcmToken;
    private final List<PasskeyCredential> credentials;
    private final Set<UUID> followedLessorIds;

    public Lessee(Builder builder) {
        this.id = builder.id;
        this.userHandle = builder.userHandle;
        this.username = builder.username;
        this.email = builder.email;
        this.fcmToken = builder.fcmToken;
        this.credentials = builder.credentials != null ?
                new ArrayList<>(builder.credentials) :
                new ArrayList<>();
        this.followedLessorIds = builder.followedLessorIds != null ? new HashSet<>(builder.followedLessorIds) : new HashSet<>();
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public byte[] getUserHandle() { return userHandle; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFcmToken() { return fcmToken; }
    public List<PasskeyCredential> getCredentials() {
        return Collections.unmodifiableList(credentials);
    }
    public Set<UUID> getFollowedLessorIds() { return Collections.unmodifiableSet(followedLessorIds); }

    public static class Builder {
        private UUID id;
        private byte[] userHandle;
        private String username;
        private String email;
        private String fcmToken;
        private List<PasskeyCredential> credentials = new ArrayList<>();
        private Set<UUID> followedLessorIds = new HashSet<>();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder userHandle(byte[] userHandle) { this.userHandle = userHandle; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fcmToken(String fcmToken) { this.fcmToken = fcmToken; return this; }

        public Builder credentials(List<PasskeyCredential> credentials) {
            this.credentials = credentials; return this;
        }

        public Builder followedLessorIds(Set<UUID> followedLessorIds) {
            this.followedLessorIds = followedLessorIds; return this;
        }

        public Builder addCredential(PasskeyCredential credential) {
            this.credentials.add(credential); return this;
        }

        public Builder addFollowedLessorId(UUID lessorId) {
            this.followedLessorIds.add(lessorId); return this;
        }

        public Lessee build() { return new Lessee(this); }
    }

}