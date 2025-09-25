package at.ac.tuwien.fundify.domain.common.exceptions;

public abstract sealed class AnnotatedCallBaseException extends FundifyException permits
    AnnotatedCallAlreadyAnnotatedException {

  public AnnotatedCallBaseException(String message) {
    super(message);
  }

  public AnnotatedCallBaseException(String message, String errorCode) {
    super(message, errorCode);
  }

}
