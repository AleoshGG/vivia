package aleosh.online.vivia.features.auth.domain.exceptions;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends AuthException {
    public TokenExpiredException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
