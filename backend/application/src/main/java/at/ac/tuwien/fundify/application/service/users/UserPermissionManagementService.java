package at.ac.tuwien.fundify.application.service.users;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.users.UserPermissionManagementUseCase;
import at.ac.tuwien.fundify.application.port.out.persistence.UserPermissionRepository;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class UserPermissionManagementService implements UserPermissionManagementUseCase {

  private final UserPermissionRepository userPermissionRepository;
  private final UserService userService;

  @Override
  @CacheInvalidateAll(cacheName = "user-permissions") // Simply invalidate all to avoid having to implement a custom cache key generator
  public UserPermissionHolder updatePermissions(UserPermissionHolder update) {
    log.infof("Update for user %s initiated by %s", update.userId(),
        userService.getCurrentUserIdAndName());
    return userPermissionRepository.upsert(update);
  }

  @Override
  @CacheResult(cacheName = "user-permissions")
  public Optional<UserPermissionHolder> getPermissions(String userId) {
    return userPermissionRepository.findByUserId(userId);
  }

  @Override
  public List<UserPermissionHolder> getAllPermissions() {
    return userPermissionRepository.findAllPermissions();
  }

  @Override
  @CacheInvalidateAll(cacheName = "user-permissions")
  public void deletePermissions(String userId) {
    log.infof("Delete for user %s initiated by %s", userId,
        userService.getCurrentUserIdAndName());
    userPermissionRepository.deleteByUserId(userId);
  }
}
