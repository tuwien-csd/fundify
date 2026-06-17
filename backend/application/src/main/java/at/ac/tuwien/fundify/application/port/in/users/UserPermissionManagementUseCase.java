package at.ac.tuwien.fundify.application.port.in.users;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import java.util.List;
import java.util.Optional;

public interface UserPermissionManagementUseCase {
  UserPermissionHolder updatePermissions(UserPermissionHolder update);
  Optional<UserPermissionHolder> getPermissions(String userId);
  List<UserPermissionHolder> getAllPermissions();
  void deletePermissions(String userId);
}
