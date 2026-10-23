package at.ac.tuwien.fundify.application.service.users;

import at.ac.tuwien.fundify.application.port.in.users.KeycloakUserLookupUseCase;
import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class KeycloakUserLookupService implements KeycloakUserLookupUseCase {

  private final KeycloakUserRepository keycloakUserRepository;

  @Override
  public List<KeycloakUser> getAllUsers() {
    return keycloakUserRepository.findAll();
  }

  @Override
  public Optional<KeycloakUser> getUserById(String id) {
    return keycloakUserRepository.findById(id);
  }
}
