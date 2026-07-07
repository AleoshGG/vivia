package aleosh.online.vivia.features.properties.media.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidMediaOperationException extends DomainException {
    public InvalidMediaOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
