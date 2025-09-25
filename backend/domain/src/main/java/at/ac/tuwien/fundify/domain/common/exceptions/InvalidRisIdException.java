package at.ac.tuwien.fundify.domain.common.exceptions;

public final class InvalidRisIdException extends FundifyException {

  private static final String errorCode = "INVALID_RIS_ID";
  private static final String message = "Invalid RIS ID format '%s', expected format 'ris:[memberId]:[idType]:[id]'";


  public InvalidRisIdException(String id) {
    super(String.format(message, id), errorCode);
  }
}