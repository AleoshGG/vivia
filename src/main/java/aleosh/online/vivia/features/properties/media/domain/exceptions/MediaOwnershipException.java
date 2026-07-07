package aleosh.online.vivia.features.properties.media.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class MediaOwnershipException extends DomainException {
    public MediaOwnershipException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
