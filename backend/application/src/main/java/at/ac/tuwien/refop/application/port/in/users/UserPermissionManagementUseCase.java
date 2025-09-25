package at.ac.tuwien.refop.application.port.in.users;

import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import java.util.Optional;

public interface UserPermissionManagementUseCase {
  UserPermissionHolder updatePermissions(UserPermissionHolder update);
  Optional<UserPermissionHolder> getPermissions(String userId);
}
