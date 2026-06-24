package aleosh.online.vivia.features.properties.properties.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidPropertyException extends DomainException {
    public InvalidPropertyException(String message) {
        super(message);
    }
}
