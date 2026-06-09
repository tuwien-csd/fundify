package at.ac.tuwien.fundify.application.port.out.keycloak;

import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import java.util.List;
import java.util.Optional;

public interface KeycloakUserRepository {

  List<KeycloakUser> findAll();

  Optional<KeycloakUser> findById(String id);
}
