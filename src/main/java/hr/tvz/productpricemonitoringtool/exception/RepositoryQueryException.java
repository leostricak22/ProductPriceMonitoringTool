package hr.tvz.productpricemonitoringtool.exception;

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
