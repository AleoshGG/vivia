package aleosh.online.vivia.features.users.users.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidPhotoException extends DomainException {
    public InvalidPhotoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
