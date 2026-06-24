package at.ac.tuwien.fundify.application.port.in.users;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import at.ac.tuwien.fundify.domain.common.UserRole;
import java.util.List;
import java.util.Optional;

public interface UserPermissionManagementUseCase {
  UserPermissionHolder updatePermissions(UserPermissionHolder update);

  /**
   * Creates a user identified by email and grants the given permissions. If no
   * Keycloak user with that email exists yet, a new (enabled) account is
   * provisioned for it before the permissions are stored; an existing account is
   * reused.
   */
  UserPermissionHolder createUserWithPermissions(String email, List<UserRole> roles,
      String affiliationId);

  Optional<UserPermissionHolder> getPermissions(String userId);
  List<UserPermissionHolder> getAllPermissions();
  void deletePermissions(String userId);
}
