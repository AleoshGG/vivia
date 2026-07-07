package aleosh.online.vivia.features.properties.media.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class MainImageDeletionException extends DomainException {
    public MainImageDeletionException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
