package aleosh.online.vivia.features.users.lessee.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidLesseeException extends DomainException {
    public InvalidLesseeException(String message) {
        super(message);
    }
}
