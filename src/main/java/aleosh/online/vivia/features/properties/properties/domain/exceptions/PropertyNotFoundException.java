package aleosh.online.vivia.features.properties.properties.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class PropertyNotFoundException extends DomainException {
    public PropertyNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
