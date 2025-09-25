package at.ac.tuwien.fundify.domain.common.exceptions;

public final class CallAlreadyPublishedException extends CallBaseException {

  private static final String message = "The call with id '%s' has already been published and cannot be modified.";
  private static final String errorCode = "CALL_ALREADY_PUBLISHED";

  public CallAlreadyPublishedException(String id) {
    super(String.format(message, id), errorCode);
  }
}
