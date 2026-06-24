package aleosh.online.vivia.features.properties.draft.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class InvalidMediaManifestException extends DomainException {
    public InvalidMediaManifestException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
