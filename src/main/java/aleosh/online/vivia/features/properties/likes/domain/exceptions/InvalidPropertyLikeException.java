package aleosh.online.vivia.features.properties.likes.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidPropertyLikeException extends DomainException {
    public InvalidPropertyLikeException(String message) {
        super(message);
    }
}
