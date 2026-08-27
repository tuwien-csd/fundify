package at.ac.tuwien.fundify.adapters.out.keycloak;

import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import at.ac.tuwien.fundify.domain.common.UserProvisioning;
import at.ac.tuwien.fundify.domain.common.exceptions.UnexpectedErrorException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

@JBossLog
@ApplicationScoped
public class KeycloakAdminUserRepository implements KeycloakUserRepository {

  private static final String UPDATE_PASSWORD_ACTION = "UPDATE_PASSWORD";

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

  @Override
  public Optional<KeycloakUser> findByEmail(String email) {
    return keycloak.realm(realm).users().searchByEmail(email, true).stream()
        .findFirst()
        .map(this::toDomain);
  }

  @Override
  public KeycloakUser create(UserProvisioning provisioning) {
    var email = provisioning.email();
    var representation = new UserRepresentation();
    representation.setUsername(email);
    representation.setEmail(email);
    representation.setFirstName(provisioning.firstName());
    representation.setLastName(provisioning.lastName());
    representation.setEnabled(true);
    // user must set a password before being able to log in
    representation.setRequiredActions(List.of(UPDATE_PASSWORD_ACTION));
    representation.setAttributes(
        Map.of("affiliation_id", List.of(provisioning.affiliationId())));

    try (Response response = keycloak.realm(realm).users().create(representation)) {
      if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
        throw new UnexpectedErrorException(
            "Failed to create Keycloak user for email %s (status %d)"
                .formatted(email, response.getStatus()));
      }
      var id = CreatedResponseUtil.getCreatedId(response);
      return findById(id).orElseThrow(() -> new UnexpectedErrorException(
          "Created Keycloak user %s could not be read back".formatted(id)));
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
