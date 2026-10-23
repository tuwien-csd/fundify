package at.ac.tuwien.fundify.application.port.out.notification;

import at.ac.tuwien.fundify.domain.common.KeycloakUser;

public interface AccountNotificationService {

  /**
   * Tells a newly provisioned user that their account exists and hands them the
   * temporary password they need for their first login. Provisioned accounts have
   * no credential otherwise, so without this mail the account cannot be used.
   */
  void sendAccountCreatedNotification(KeycloakUser user, String temporaryPassword);

}
