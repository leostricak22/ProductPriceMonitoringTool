package hr.tvz.productpricemonitoringtool.exception;

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
