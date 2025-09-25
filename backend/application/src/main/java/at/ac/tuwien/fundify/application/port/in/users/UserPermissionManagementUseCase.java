package at.ac.tuwien.fundify.application.port.in.users;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import java.util.Optional;

public interface UserPermissionManagementUseCase {
  UserPermissionHolder updatePermissions(UserPermissionHolder update);
  Optional<UserPermissionHolder> getPermissions(String userId);
}
