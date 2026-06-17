package aleosh.online.vivia.features.auth.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class CredentialNotFoundException extends DomainException {
    public CredentialNotFoundException(String message) { super(message); }
}
