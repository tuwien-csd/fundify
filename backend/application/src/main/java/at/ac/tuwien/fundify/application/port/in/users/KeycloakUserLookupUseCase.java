package at.ac.tuwien.fundify.application.port.in.users;

import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import java.util.List;
import java.util.Optional;

public interface KeycloakUserLookupUseCase {

  List<KeycloakUser> getAllUsers();

  Optional<KeycloakUser> getUserById(String id);
}
