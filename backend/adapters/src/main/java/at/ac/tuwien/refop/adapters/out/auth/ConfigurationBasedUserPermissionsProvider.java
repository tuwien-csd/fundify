package at.ac.tuwien.refop.adapters.out.auth;

import at.ac.tuwien.refop.adapters.in.rest.auth.PermissionsConfig;
import at.ac.tuwien.refop.application.port.out.permissions.UserPermissionsProvider;
import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@Named("configPermissionProvider")
@RequiredArgsConstructor
@JBossLog
public class ConfigurationBasedUserPermissionsProvider implements UserPermissionsProvider {

  private final PermissionsConfig permissionsConfig;

  @Override
  public Optional<UserPermissionHolder> getUserPermissions(String userId) {
    var source = permissionsConfig.permissionMappingByUserId().get(userId);
    if (source == null) {
      return Optional.empty();
    }
    return Optional.of(new UserPermissionHolder(
        source.userId(),
        source.roles(),
        source.affiliationId()
    ));
  }
}
