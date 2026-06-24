package aleosh.online.vivia.features.properties.draft.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidPropertyDraftException extends DomainException {
    public InvalidPropertyDraftException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
