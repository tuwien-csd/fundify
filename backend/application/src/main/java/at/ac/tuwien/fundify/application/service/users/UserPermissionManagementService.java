package at.ac.tuwien.fundify.application.service.users;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.users.UserPermissionManagementUseCase;
import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
import at.ac.tuwien.fundify.application.port.out.notification.AccountNotificationService;
import at.ac.tuwien.fundify.application.port.out.persistence.UserPermissionRepository;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import at.ac.tuwien.fundify.domain.common.UserProvisioning;
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
  private final KeycloakUserRepository keycloakUserRepository;
  private final UserService userService;
  private final AccountNotificationService accountNotificationService;
  private final TemporaryPasswordGenerator temporaryPasswordGenerator;

  @Override
  @CacheInvalidateAll(cacheName = "user-permissions") // Simply invalidate all to avoid having to implement a custom cache key generator
  public UserPermissionHolder updatePermissions(UserPermissionHolder update) {
    log.infof("Update for user %s initiated by %s", update.userId(),
        userService.getCurrentUserIdAndName());
    return userPermissionRepository.upsert(update);
  }

  @Override
  @CacheInvalidateAll(cacheName = "user-permissions")
  public UserPermissionHolder createUserWithPermissions(UserProvisioning provisioning) {
    var email = provisioning.email();
    var existing = keycloakUserRepository.findByEmail(email);

    KeycloakUser user;
    if (existing.isPresent()) {
      // An existing account keeps its name and its password; only permissions change.
      user = existing.get();
    } else {
      log.infof("No Keycloak user for email %s, provisioning a new account (initiated by %s)",
          email, userService.getCurrentUserIdAndName());
      var temporaryPassword = temporaryPasswordGenerator.generate();
      user = keycloakUserRepository.create(provisioning, temporaryPassword);
      // Without this mail the user never learns the password and cannot log in at all.
      accountNotificationService.sendAccountCreatedNotification(user, temporaryPassword);
    }

    return updatePermissions(
        new UserPermissionHolder(user.id(), provisioning.roles(), provisioning.affiliationId()));
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
