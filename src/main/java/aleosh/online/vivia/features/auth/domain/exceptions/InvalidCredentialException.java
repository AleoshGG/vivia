package aleosh.online.vivia.features.auth.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidCredentialException extends DomainException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}
