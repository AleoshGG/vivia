package aleosh.online.vivia.features.users.users.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class UserAlreadyExistsException extends DomainException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
