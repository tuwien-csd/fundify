package at.ac.tuwien.fundify.adapters.out.auth;

import at.ac.tuwien.fundify.application.port.in.users.UserPermissionManagementUseCase;
import at.ac.tuwien.fundify.application.port.out.permissions.UserPermissionsProvider;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.Optional;

@ApplicationScoped
@Named("dbProvider")
public class DatabaseBasedUserPermissionsProvider implements UserPermissionsProvider {

  DatabaseBasedUserPermissionsProvider(
      @Named("configPermissionProvider")
      ConfigurationBasedUserPermissionsProvider configUserPermissionsProvider,
      UserPermissionManagementUseCase userPermissionManagementUseCase) {
    this.configUserPermissionsProvider = configUserPermissionsProvider;
    this.userPermissionManagementUseCase = userPermissionManagementUseCase;
  }

  private final ConfigurationBasedUserPermissionsProvider configUserPermissionsProvider;
  private final UserPermissionManagementUseCase userPermissionManagementUseCase;

  /**
   * Retrieves the user permissions from the database. Falls back to the configuration-based
   * permissions if no user permissions are found in the database.
   */
  @Override
  public Optional<UserPermissionHolder> getUserPermissions(String userId){
    return userPermissionManagementUseCase
        .getPermissions(userId)
        .or(() -> configUserPermissionsProvider.getUserPermissions(userId));
  }
}
