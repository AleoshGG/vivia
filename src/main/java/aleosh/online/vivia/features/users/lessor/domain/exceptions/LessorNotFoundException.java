package aleosh.online.vivia.features.users.lessor.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class LessorNotFoundException extends DomainException {
    public LessorNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
