package aleosh.online.vivia.features.properties.media.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class MediaNotFoundException extends DomainException {
    public MediaNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
