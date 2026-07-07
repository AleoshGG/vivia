package aleosh.online.vivia.features.properties.properties.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class PropertyOwnershipException extends DomainException {
    public PropertyOwnershipException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
