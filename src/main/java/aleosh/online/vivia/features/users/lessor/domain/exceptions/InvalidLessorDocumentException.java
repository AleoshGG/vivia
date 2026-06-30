package aleosh.online.vivia.features.users.lessor.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidLessorDocumentException extends DomainException {
    public InvalidLessorDocumentException(String message) {
        super(message);
    }
}
