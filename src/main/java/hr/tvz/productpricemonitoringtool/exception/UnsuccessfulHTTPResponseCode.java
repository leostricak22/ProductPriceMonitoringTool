package hr.tvz.productpricemonitoringtool.exception;

public class UnsuccessfulHTTPResponseCode extends Exception {
    public UnsuccessfulHTTPResponseCode(String message) {
        super(message);
    }

  public UnsuccessfulHTTPResponseCode(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsuccessfulHTTPResponseCode(Throwable cause) {
    super(cause);
  }

  public UnsuccessfulHTTPResponseCode(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public UnsuccessfulHTTPResponseCode() {
  }
}
