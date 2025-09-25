package at.ac.tuwien.refop.domain.common.exceptions;

public abstract sealed class CallBaseException extends FundifyException permits
    CallAlreadyPublishedException, CallRisIdIsSetException {

  public CallBaseException(String message) {
    super(message);
  }

  public CallBaseException(String message, String errorCode) {
    super(message, errorCode);
  }

}
