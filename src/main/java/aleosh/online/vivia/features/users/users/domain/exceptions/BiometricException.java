package aleosh.online.vivia.features.users.users.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class BiometricException extends DomainException {
    public BiometricException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public BiometricException(String message, HttpStatus status) {
        super(message, status);
    }
}
