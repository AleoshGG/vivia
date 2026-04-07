package aleosh.online.vivia.features.users.lessor.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.valueobjects.PasskeyCredential;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lessor {

    private final UUID id;
    private final byte[] userHandle; // Nuevo campo
    private final String firstName;
    private final String lastName;
    private final String companyName;
    private final String password;
    private final String phoneNumber;
    private final List<PasskeyCredential> credentials;

    public Lessor(Builder builder) {
        this.id = builder.id;
        this.userHandle = builder.userHandle;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.companyName = builder.companyName;
        this.password = builder.password;
        this.phoneNumber = builder.phoneNumber;
        this.credentials = builder.credentials != null ?
                new ArrayList<>(builder.credentials) :
                new ArrayList<>();
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public byte[] getUserHandle() { return userHandle; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCompanyName() { return companyName; }
    public String getPassword() { return password; }
    public String getPhoneNumber() { return  phoneNumber; }
    public List<PasskeyCredential> getCredentials() {
        return Collections.unmodifiableList(credentials);
    }

    public static class Builder {
        private UUID id;
        private byte[] userHandle;
        private String firstName;
        private String lastName;
        private String companyName;
        private String password;
        private String phoneNumber;
        private List<PasskeyCredential> credentials = new ArrayList<>();

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder userHandle(byte[] userHandle) { this.userHandle = userHandle; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this;}

        public Builder credentials(List<PasskeyCredential> credentials) {
            this.credentials = credentials; return this;
        }

        public Builder addCredential(PasskeyCredential credential) {
            this.credentials.add(credential); return this;
        }

        public Lessor build() { return new Lessor(this); }
    }
}