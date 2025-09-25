package at.ac.tuwien.refop.application.port.out.persistence;

import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import java.util.Optional;

public interface UserPermissionRepository {
  UserPermissionHolder upsert(UserPermissionHolder user);
  Optional<UserPermissionHolder> findByUserId(String userId);
}
