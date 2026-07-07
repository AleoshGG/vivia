package aleosh.online.vivia.features.users.admin.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class FcmSubscriptionException extends DomainException {
    public FcmSubscriptionException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
