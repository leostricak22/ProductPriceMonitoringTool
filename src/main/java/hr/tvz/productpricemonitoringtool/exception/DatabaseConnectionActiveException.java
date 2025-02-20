package hr.tvz.productpricemonitoringtool.exception;

/**
 * Exception for the database connection active.
 * Thrown when the database connection is active.
 */
public class DatabaseConnectionActiveException extends Exception {
    public DatabaseConnectionActiveException(String message) {
        super(message);
    }

  public DatabaseConnectionActiveException(String message, Throwable cause) {
    super(message, cause);
  }

  public DatabaseConnectionActiveException(Throwable cause) {
    super(cause);
  }

  public DatabaseConnectionActiveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public DatabaseConnectionActiveException() {
  }
}
