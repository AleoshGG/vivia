package aleosh.online.vivia.features.users.users.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(String message) { super(message); }
}
