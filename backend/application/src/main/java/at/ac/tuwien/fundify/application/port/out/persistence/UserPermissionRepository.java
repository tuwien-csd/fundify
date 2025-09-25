package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import java.util.Optional;

public interface UserPermissionRepository {
  UserPermissionHolder upsert(UserPermissionHolder user);
  Optional<UserPermissionHolder> findByUserId(String userId);
}
