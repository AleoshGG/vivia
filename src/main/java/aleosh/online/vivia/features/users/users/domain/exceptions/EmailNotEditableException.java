package aleosh.online.vivia.features.users.users.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class EmailNotEditableException extends DomainException {
    public EmailNotEditableException(String message) {
        super(message);
    }
}
