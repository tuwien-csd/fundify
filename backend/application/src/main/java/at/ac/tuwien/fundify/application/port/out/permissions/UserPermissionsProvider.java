package at.ac.tuwien.fundify.application.port.out.permissions;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import java.util.Optional;

public interface UserPermissionsProvider {

  Optional<UserPermissionHolder> getUserPermissions(String userId);

}
