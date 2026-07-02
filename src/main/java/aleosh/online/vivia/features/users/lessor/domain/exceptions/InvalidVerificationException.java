package aleosh.online.vivia.features.users.lessor.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidVerificationException extends DomainException {
    public InvalidVerificationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
