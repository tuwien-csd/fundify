package at.ac.tuwien.fundify.application.port.in.users;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import at.ac.tuwien.fundify.domain.common.UserProvisioning;
import java.util.List;
import java.util.Optional;

public interface UserPermissionManagementUseCase {
  UserPermissionHolder updatePermissions(UserPermissionHolder update);

  /**
   * Creates a user identified by email and grants the given permissions. If no
   * Keycloak user with that email exists yet, a new (enabled) account is
   * provisioned for it before the permissions are stored; an existing account is
   * reused as-is, so its given name and surname are deliberately left untouched
   * (names are set once, at provisioning time).
   */
  UserPermissionHolder createUserWithPermissions(UserProvisioning provisioning);

  Optional<UserPermissionHolder> getPermissions(String userId);
  List<UserPermissionHolder> getAllPermissions();
  void deletePermissions(String userId);
}
