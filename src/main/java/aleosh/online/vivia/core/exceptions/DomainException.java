package aleosh.online.vivia.core.exceptions;

import org.springframework.http.HttpStatus;

public abstract class DomainException extends RuntimeException {
    private final HttpStatus status;

    public DomainException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public DomainException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
