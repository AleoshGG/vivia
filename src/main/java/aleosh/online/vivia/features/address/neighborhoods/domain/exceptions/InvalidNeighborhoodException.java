package aleosh.online.vivia.features.address.neighborhoods.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidNeighborhoodException extends DomainException {
    public InvalidNeighborhoodException(String message) {
        super(message);
    }
}
