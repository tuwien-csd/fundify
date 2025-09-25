package at.ac.tuwien.refop.domain.common.exceptions;

public final class RisIdMustNotBeSetException extends FundifyException {

  private static final String errorCode = "RIS_ID_MUST_NOT_BE_SET_DURING_CREATION";
  private static final String message = "RIS ID must not be set when adding a new %s.";


  public RisIdMustNotBeSetException(String entityName) {
    super(String.format(message, entityName), errorCode);
  }
}