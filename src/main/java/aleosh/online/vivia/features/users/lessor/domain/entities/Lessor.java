package aleosh.online.vivia.features.users.lessor.domain.entities;

import aleosh.online.vivia.features.users.lessor.domain.exceptions.InvalidLessorException;
import aleosh.online.vivia.features.users.lessor.domain.objectvalues.VerificationStatus;
import java.util.UUID;
import java.util.regex.Pattern;

public class Lessor {
    private final UUID id;
    private final String phoneNumber;
    private final VerificationStatus verificationStatus;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    private Lessor(Builder builder) {
        validate(builder);
        this.id = builder.id;
        this.phoneNumber = builder.phoneNumber;
        this.verificationStatus = builder.verificationStatus != null
                ? builder.verificationStatus
                : VerificationStatus.UNVERIFIED;
    }

    private void validate(Builder builder) {
        if (builder.id == null)
            throw new InvalidLessorException("Lessor ID is required");

        if (builder.phoneNumber == null || !PHONE_PATTERN.matcher(builder.phoneNumber).matches())
            throw new InvalidLessorException("Invalid phone number format (must be 10-15 digits)");
    }

    public static Builder builder() { return new Builder(); }

    public UUID getId() { return id; }
    public String getPhoneNumber() { return phoneNumber; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }

    public static class Builder {
        private UUID id;
        private String phoneNumber;
        private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder verificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; return this; }

        public Lessor build() { return new Lessor(this); }
    }
}
