package at.ac.tuwien.fundify.domain.common.exceptions;

public final class UnexpectedErrorException extends FundifyException {
  private static final String errorCode = "UNEXPECTED_ERROR";
  private static final String defaultMessage = "An unexpected error occurred.";


  public UnexpectedErrorException(String message) {
    super(message, errorCode);
  }
  public UnexpectedErrorException() {
    super(defaultMessage, errorCode);
  }
}