package hr.tvz.productpricemonitoringtool.exception;

/**
 * Exception for the repository access.
 * Thrown when the repository access fails.
 */
public class RepositoryAccessException extends RuntimeException {
    public RepositoryAccessException(String message) {
        super(message);
    }

    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryAccessException(Throwable cause) {
        super(cause);
    }

    public RepositoryAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RepositoryAccessException() {
    }
}
