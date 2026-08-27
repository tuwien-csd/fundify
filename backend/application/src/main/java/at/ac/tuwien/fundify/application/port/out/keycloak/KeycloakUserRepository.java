package at.ac.tuwien.fundify.application.port.out.keycloak;

import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import at.ac.tuwien.fundify.domain.common.UserProvisioning;
import java.util.List;
import java.util.Optional;

public interface KeycloakUserRepository {

  List<KeycloakUser> findAll();

  Optional<KeycloakUser> findById(String id);

  Optional<KeycloakUser> findByEmail(String email);

  /**
   * Creates a new (enabled) Keycloak user from the given provisioning data and
   * sets the temporary password the user needs for their first login. Used to
   * provision accounts that an admin grants permissions to before the person has
   * ever logged in.
   *
   * <p>The password is marked temporary, so Keycloak forces the user to replace it
   * on first login.
   */
  KeycloakUser create(UserProvisioning provisioning, String temporaryPassword);
}
