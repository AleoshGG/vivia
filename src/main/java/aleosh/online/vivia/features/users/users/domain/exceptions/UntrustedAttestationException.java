package aleosh.online.vivia.features.users.users.domain.exceptions;

import org.springframework.http.HttpStatus;

public class UntrustedAttestationException extends BiometricException {
    public UntrustedAttestationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
