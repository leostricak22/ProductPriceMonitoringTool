package hr.tvz.productpricemonitoringtool.exception;

/**
 * Exception for the repository query.
 * Thrown when the repository query fails.
 */
public class RepositoryQueryException extends RuntimeException {
    public RepositoryQueryException(String message) {
        super(message);
    }

    public RepositoryQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryQueryException(Throwable cause) {
        super(cause);
    }

    public RepositoryQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RepositoryQueryException() {
    }
}
