package aleosh.online.vivia.features.users.users.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class EmailNotEditableException extends DomainException {
    public EmailNotEditableException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
