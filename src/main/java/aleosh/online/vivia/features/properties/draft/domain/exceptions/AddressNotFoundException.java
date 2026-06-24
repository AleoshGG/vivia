package aleosh.online.vivia.features.properties.draft.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class AddressNotFoundException extends DomainException {
    public AddressNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
