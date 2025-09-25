package at.ac.tuwien.refop.adapters.in.rest.auth;

import at.ac.tuwien.refop.application.port.out.permissions.UserPermissionsProvider;
import at.ac.tuwien.refop.domain.common.UserRole;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Augments the SecurityIdentity to derive roles from PermissionsConfig
 * instead of relying on roles present in the JWT token.
 */
@ApplicationScoped
public class PermissionsBasedRoleAugmentor implements SecurityIdentityAugmentor {

  private final UserPermissionsProvider permissionsProvider;

  public PermissionsBasedRoleAugmentor(@Named("dbProvider") UserPermissionsProvider permissionsProvider) {
    this.permissionsProvider = permissionsProvider;
  }

  @Override
  public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
    // Determine current user id
    String userId = extractUserId(identity);

    // Execute potentially blocking work off the event-loop
    return context.runBlocking(() -> {
      if (userId == null) {
        return identity;
      }

      var mapping = permissionsProvider.getUserPermissions(userId);
      if (mapping.isEmpty()) {
        // No mapping found, keep the original identity as-is
        return identity;
      }

      // Map configured roles (Role enum) to canonical role strings
      Set<String> roles = mapping.get().roles().stream()
          .filter(Objects::nonNull)
          .map(UserRole::toString)
          .collect(Collectors.toCollection(LinkedHashSet::new));

      // Build a new identity that keeps everything but replaces roles
      QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder();
      builder.setPrincipal(identity.getPrincipal());
      builder.addAttributes(identity.getAttributes());
      identity.getCredentials().forEach(builder::addCredential);
      if (identity.isAnonymous()) {
        builder.setAnonymous(true);
      }
      // Replace roles with those from configuration
      builder.addRoles(roles);

      return builder.build();
    });
  }

  private String extractUserId(SecurityIdentity identity) {
    var principal = identity.getPrincipal();
    if (principal instanceof JsonWebToken jwt) {
      String sub = jwt.getClaim("sub");
      if (sub != null) {
        return sub;
      }
    }
    return principal != null ? principal.getName() : null;
  }
}
