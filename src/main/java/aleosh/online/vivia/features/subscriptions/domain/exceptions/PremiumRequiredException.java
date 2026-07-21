package aleosh.online.vivia.features.subscriptions.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class PremiumRequiredException extends DomainException {
    public PremiumRequiredException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
