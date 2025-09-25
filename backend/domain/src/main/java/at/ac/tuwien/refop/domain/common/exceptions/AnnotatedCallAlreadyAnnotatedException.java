package at.ac.tuwien.refop.domain.common.exceptions;

public final class AnnotatedCallAlreadyAnnotatedException extends AnnotatedCallBaseException {

  private static final String message = "The call you are trying to annotate is already annotated.";
  private static final String errorCode = "CALL_ALREADY_ANNOTATED";

  public AnnotatedCallAlreadyAnnotatedException() {
    super(message, errorCode);
  }
}
