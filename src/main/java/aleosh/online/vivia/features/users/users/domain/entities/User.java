package aleosh.online.vivia.features.users.users.domain.entities;

import aleosh.online.vivia.features.users.users.domain.exceptions.InvalidUserException;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

public class User {
    private final UUID id;
    private final String name;
    private final String paternalSurname;
    private final String maternalSurname;
    private final String email;
    private final String photoUrl;

    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private User(Builder builder) {
        validate(builder);
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = builder.name;
        this.paternalSurname = builder.paternalSurname;
        this.maternalSurname = builder.maternalSurname;
        this.email = builder.email;
        this.photoUrl = builder.photoUrl;
        this.createdAt = builder.createdAt != null ? builder.createdAt : OffsetDateTime.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : OffsetDateTime.now();
    }

    private void validate(Builder builder) {
        if (builder.name == null || builder.name.trim().isEmpty()) 
            throw new InvalidUserException("User name cannot be empty");
        
        if (builder.paternalSurname == null || builder.paternalSurname.trim().isEmpty()) 
            throw new InvalidUserException("Paternal surname cannot be empty");

        if (builder.email == null || !EMAIL_PATTERN.matcher(builder.email).matches()) 
            throw new InvalidUserException("Invalid email format");
            
        if (builder.photoUrl == null || builder.photoUrl.trim().isEmpty())
            throw new InvalidUserException("Photo URL is required");
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getPaternalSurname() { return paternalSurname; }
    public String getMaternalSurname() { return maternalSurname; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
        private String name;
        private String paternalSurname;
        private String maternalSurname;
        private String email;
        private String photoUrl;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder paternalSurname(String paternalSurname) { this.paternalSurname = paternalSurname; return this; }
        public Builder maternalSurname(String maternalSurname) { this.maternalSurname = maternalSurname; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }
        public Builder createdAt(OffsetDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public User build() { return new User(this); }
    }
}
