package aleosh.online.vivia.features.users.admin.domain.entities;

import aleosh.online.vivia.features.users.admin.domain.exceptions.AdminNotFoundException;

import java.util.UUID;

public class Admin {
    private final UUID id;

    private Admin(Builder builder) {
        validate(builder);
        this.id = builder.id;
    }

    private void validate(Builder builder) {
        if (builder.id == null)
            throw new AdminNotFoundException("Admin ID is required");
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }

    public static class Builder {
        private UUID id;

        public Builder id(UUID id) { this.id = id; return this; }

        public Admin build() { return new Admin(this); }
    }
}
