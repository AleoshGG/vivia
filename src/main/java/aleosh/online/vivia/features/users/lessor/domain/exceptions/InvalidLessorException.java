package aleosh.online.vivia.features.users.lessor.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidLessorException extends DomainException {
    public InvalidLessorException(String message) {
        super(message);
    }
}
