package at.ac.tuwien.refop.domain.common.exceptions;

public final class CallRisIdIsSetException extends CallBaseException {

  private static final String message = "RisId must not be set when adding a new call.";
  private static final String errorCode = "CALL_RIS_ID_MUST_NOT_BE_SET_DURING_CREATION";

  public CallRisIdIsSetException() {
    super(message, errorCode);
  }
}
