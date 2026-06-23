package aleosh.online.vivia.features.properties.draft.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class PropertyTypeNotFoundException extends DomainException {
    public PropertyTypeNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
