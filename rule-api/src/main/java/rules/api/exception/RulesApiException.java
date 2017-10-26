/** */
package rules.api.exception;

/**
 * Exception class for rules engine.It implements RuntimeException
 *
 * @author chandresh.mishra
 */
public class RulesApiException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Call Runtime exception with message
   *
   * @param message - Error message
   */
  public RulesApiException(String message) {
    super(message);
  }

  /**
   * Call Runtime exception with message and cause.
   *
   * @param message - Error Message
   * @param cause - Exception Cause
   */
  public RulesApiException(String message, Throwable cause) {
    super(message, cause);
  }

  /** Returns a string consisting of the name, code, message and cause for this exception. */
  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("\nName : " + this.getClass().getSimpleName());
    str.append("\nMessage : " + this.getMessage());
    if (this.getCause() != null) str.append("\nCause : " + this.getCause());

    return str.toString();
  }
}
