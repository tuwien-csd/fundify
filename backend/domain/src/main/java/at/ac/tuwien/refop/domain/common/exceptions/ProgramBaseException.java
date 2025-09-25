package at.ac.tuwien.refop.domain.common.exceptions;

public abstract sealed class ProgramBaseException extends FundifyException permits
    ProgramAlreadyPublishedException {

  public ProgramBaseException(String message) {
    super(message);
  }

  public ProgramBaseException(String message, String errorCode) {
    super(message, errorCode);
  }

}
