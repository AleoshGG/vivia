package aleosh.online.vivia.features.address.address.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;

public class InvalidAddressException extends DomainException {
    public InvalidAddressException(String message) {
        super(message);
    }
}
