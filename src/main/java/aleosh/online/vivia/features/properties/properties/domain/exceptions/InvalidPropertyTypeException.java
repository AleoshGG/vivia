package aleosh.online.vivia.features.properties.properties.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidPropertyTypeException extends DomainException {
    public InvalidPropertyTypeException(String message) {
        super(message);
    }
}
