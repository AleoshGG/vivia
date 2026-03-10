package aleosh.online.vivia.features.users.lessee.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.valueobjects.PasskeyCredential;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lessee {

    private final UUID id;
    private final String username;
    private final String email;

    public Lessee(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }

    public static class Builder {
        private UUID id;
        private String username;
        private String email;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Lessee build() { return new Lessee(this); }
    }

}
