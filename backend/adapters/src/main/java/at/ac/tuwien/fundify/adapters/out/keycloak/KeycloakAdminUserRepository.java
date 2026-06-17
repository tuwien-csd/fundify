package at.ac.tuwien.fundify.adapters.out.keycloak;

import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;

@JBossLog
@ApplicationScoped
public class KeycloakAdminUserRepository implements KeycloakUserRepository {

  private final Keycloak keycloak;
  private final String realm;

  public KeycloakAdminUserRepository(
      Keycloak keycloak,
      @ConfigProperty(name = "fundify.keycloak.admin.realm") String realm) {
    this.keycloak = keycloak;
    this.realm = realm;
  }

  @Override
  public List<KeycloakUser> findAll() {
    return keycloak.realm(realm).users().list(0, 99999).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public Optional<KeycloakUser> findById(String id) {
    try {
      var representation = keycloak.realm(realm).users().get(id).toRepresentation();
      return Optional.ofNullable(representation).map(this::toDomain);
    } catch (NotFoundException e) {
      return Optional.empty();
    }
  }

  private KeycloakUser toDomain(org.keycloak.representations.idm.UserRepresentation rep) {
    return new KeycloakUser(
        rep.getId(),
        rep.getUsername(),
        rep.getEmail(),
        rep.getFirstName(),
        rep.getLastName(),
        Boolean.TRUE.equals(rep.isEnabled()));
  }
}
