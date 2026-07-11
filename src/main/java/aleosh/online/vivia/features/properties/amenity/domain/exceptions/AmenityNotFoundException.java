package aleosh.online.vivia.features.properties.amenity.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class AmenityNotFoundException extends DomainException {
    public AmenityNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
