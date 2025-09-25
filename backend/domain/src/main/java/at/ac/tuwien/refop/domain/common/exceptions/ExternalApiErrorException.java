package at.ac.tuwien.refop.domain.common.exceptions;

public final class ExternalApiErrorException extends FundifyException {

  private static final String errorCode = "EXTERNAL_API_EXCEPTION";
  public int externalApiStatusCode;


  public ExternalApiErrorException(String message, int externalApiStatusCode) {
    super(message, errorCode);
    this.externalApiStatusCode = externalApiStatusCode;
  }
}