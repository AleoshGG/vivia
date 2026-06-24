package aleosh.online.vivia.features.properties.amenity.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidAmenityException extends DomainException {
    public InvalidAmenityException(String message) {
        super(message);
    }
}
