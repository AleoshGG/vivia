package aleosh.online.vivia.features.auth.domain.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidChallengeException extends AuthException {
    public InvalidChallengeException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
