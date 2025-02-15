package hr.tvz.productpricemonitoringtool.exception;

public class InvalidSessionException extends Exception {
    public InvalidSessionException(String message) {
        super(message);
    }

  public InvalidSessionException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidSessionException(Throwable cause) {
    super(cause);
  }

  public InvalidSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public InvalidSessionException() {
  }
}
