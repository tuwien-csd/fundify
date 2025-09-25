package at.ac.tuwien.refop.domain.common.exceptions;

public final class NoUserPermissionsConfiguredException extends FundifyException {

  private static final String errorCode = "MISSING_USER_PERMISSIONS";
  private static final String message = "There are no permissions configured for the requested user with id '%s'. Check your user account and reach out to the system administrator if necessary.";


  public NoUserPermissionsConfiguredException(String userId) {
    super(String.format(message, userId), errorCode);
  }
}