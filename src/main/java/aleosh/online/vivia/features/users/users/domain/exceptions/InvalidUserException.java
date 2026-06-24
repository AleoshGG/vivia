package aleosh.online.vivia.features.users.users.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidUserException extends DomainException {
    public InvalidUserException(String message) {
        super(message);
    }
}
