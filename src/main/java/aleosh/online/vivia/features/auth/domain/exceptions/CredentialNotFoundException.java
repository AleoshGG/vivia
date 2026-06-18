package aleosh.online.vivia.features.auth.domain.exceptions;

import aleosh.online.vivia.core.exceptions.DomainException;
import org.springframework.http.HttpStatus;

public class CredentialNotFoundException extends DomainException {
    public CredentialNotFoundException(String message) { 
        super(message, HttpStatus.NOT_FOUND); 
    }
}
