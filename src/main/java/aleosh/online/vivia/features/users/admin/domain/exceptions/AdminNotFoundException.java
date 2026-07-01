package aleosh.online.vivia.features.users.admin.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class AdminNotFoundException extends DomainException {
    public AdminNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
