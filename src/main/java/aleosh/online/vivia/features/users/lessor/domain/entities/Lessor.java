package aleosh.online.vivia.features.users.lessor.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.valueobjects.PasskeyCredential;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lessor {

    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final String companyName;
    private final List<PasskeyCredential> credentials;

    public Lessor(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.companyName = builder.companyName;
        this.credentials = builder.credentials != null ?
                new ArrayList<>(builder.credentials) :
                new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCompanyName() { return companyName; }
    public List<PasskeyCredential> getCredentials() {
        return Collections.unmodifiableList(credentials);
    }

    public static class Builder {
        private UUID id;
        private String firstName;
        private String lastName;
        private String companyName;
        private List<PasskeyCredential> credentials = new ArrayList<>();

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder credentials(List<PasskeyCredential> credentials) {
            this.credentials = credentials;
            return this;
        }

        public Builder addCredential(PasskeyCredential credential) {
            this.credentials.add(credential);
            return this;
        }

        public Lessor build() { return new Lessor(this); }
    }

}
