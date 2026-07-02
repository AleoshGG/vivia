package aleosh.online.vivia.features.reports.domain.exceptions;

public class ReportAlreadyExistsException extends RuntimeException {
    public ReportAlreadyExistsException(String message) {
        super(message);
    }
}
