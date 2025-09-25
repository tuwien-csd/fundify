package at.ac.tuwien.refop.application.port.out.permissions;

import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import java.util.Optional;

public interface UserPermissionsProvider {

  Optional<UserPermissionHolder> getUserPermissions(String userId);

}
