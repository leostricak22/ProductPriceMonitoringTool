package hr.tvz.productpricemonitoringtool.exception;

/**
 * Exception for the FXMLLoader.
 * Thrown when the FXMLLoader fails.
 */
public class FXMLLoaderException extends RuntimeException {
    public FXMLLoaderException(String message) {
        super(message);
    }

    public FXMLLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public FXMLLoaderException(Throwable cause) {
        super(cause);
    }

    public FXMLLoaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FXMLLoaderException() {
    }
}
