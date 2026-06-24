package aleosh.online.vivia.features.properties.draft.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class NeighborhoodNotFoundException extends DomainException {
    public NeighborhoodNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
