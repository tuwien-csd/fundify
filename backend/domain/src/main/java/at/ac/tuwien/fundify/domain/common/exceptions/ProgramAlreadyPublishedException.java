package at.ac.tuwien.fundify.domain.common.exceptions;

public final class ProgramAlreadyPublishedException extends ProgramBaseException {

  private static final String message = "The program with id '%s' has already been published and cannot be modified.";
  private static final String errorCode = "PROGRAM_ALREADY_PUBLISHED";

  public ProgramAlreadyPublishedException(String id) {
    super(String.format(message, id), errorCode);
  }
}
