package at.ac.tuwien.fundify.domain.common.exceptions;

public final class InsufficientPermissionsException extends FundifyException {

  private static final String errorCode = "INSUFFICIENT_PERMISSIONS";
  private static final String message = "The current user does not have permissions to modify the entity with id '%s'";


  public InsufficientPermissionsException(String entityId) {
    super(String.format(message, entityId), errorCode);
  }
  public InsufficientPermissionsException() {
    super(String.format(message, "new call"), errorCode);
  }
}