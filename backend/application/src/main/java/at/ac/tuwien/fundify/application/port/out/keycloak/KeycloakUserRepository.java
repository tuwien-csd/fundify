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
   * Creates a new (enabled) Keycloak user from the given provisioning data.
   * Used to provision accounts that an admin grants permissions
   * to before the person has ever logged in.
   */
  KeycloakUser create(UserProvisioning provisioning);
}
