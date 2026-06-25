package aleosh.online.vivia.features.users.lessee.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class LesseeNotFoundException extends DomainException {
    public LesseeNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
