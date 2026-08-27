package at.ac.tuwien.fundify.application.service.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
import at.ac.tuwien.fundify.application.port.out.notification.AccountNotificationService;
import at.ac.tuwien.fundify.application.port.out.persistence.UserPermissionRepository;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import at.ac.tuwien.fundify.domain.common.UserProvisioning;
import at.ac.tuwien.fundify.domain.common.UserRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPermissionManagementServiceTest {

  @Mock
  private UserPermissionRepository userPermissionRepository;

  @Mock
  private KeycloakUserRepository keycloakUserRepository;

  @Mock
  private UserService userService;

  @Mock
  private AccountNotificationService accountNotificationService;

  private UserPermissionManagementService service;

  @BeforeEach
  void setUp() {
    service = new UserPermissionManagementService(userPermissionRepository,
        keycloakUserRepository, userService, accountNotificationService,
        new TemporaryPasswordGenerator());
  }

  @Test
  void getAllPermissions_delegatesToRepository() {
    // arrange
    List<UserPermissionHolder> permissions = List.of(
        new UserPermissionHolder("user-1", List.of(UserRole.ADMIN), "andamp"),
        new UserPermissionHolder("user-2", List.of(UserRole.ANNOTATOR), "univie"));
    when(userPermissionRepository.findAllPermissions()).thenReturn(permissions);

    // act
    List<UserPermissionHolder> result = service.getAllPermissions();

    // assert
    assertEquals(permissions, result);
    verify(userPermissionRepository).findAllPermissions();
    verifyNoInteractions(userService);
  }

  @Test
  void createUserWithPermissions_existingUser_upsertsAgainstExistingId() {
    // arrange
    var affiliationId = "univie";
    var roles = List.of(UserRole.ANNOTATOR);
    when(keycloakUserRepository.findByEmail("known@univie.ac.at"))
        .thenReturn(Optional.of(
            new KeycloakUser("user-1", "known@univie.ac.at", "known@univie.ac.at", null, null,
                true)));
    var expected = new UserPermissionHolder("user-1", roles, affiliationId);
    when(userPermissionRepository.upsert(expected)).thenReturn(expected);

    // act
    var result = service.createUserWithPermissions(new UserProvisioning(
        "known@univie.ac.at", "Known", "User", roles, affiliationId));

    // assert
    assertEquals(expected, result);
    verify(userPermissionRepository).upsert(expected);
    // an existing account is reused as-is: its name and password are not overwritten,
    // and the person is not mailed again
    verify(keycloakUserRepository, never()).create(any(), any());
    verifyNoInteractions(accountNotificationService);
  }

  @Test
  void createUserWithPermissions_unknownUser_provisionsKeycloakUserFirst() {
    // arrange
    var affiliationId = "univie";
    var roles = List.of(UserRole.FUNDER);
    var provisioning = new UserProvisioning(
        "new@univie.ac.at", "New", "User", roles, affiliationId);
    var created = new KeycloakUser("new-id", "new@univie.ac.at", "new@univie.ac.at", "New", "User",
        true);
    when(keycloakUserRepository.findByEmail("new@univie.ac.at")).thenReturn(Optional.empty());
    when(keycloakUserRepository.create(eq(provisioning), anyString())).thenReturn(created);
    var expected = new UserPermissionHolder("new-id", roles, affiliationId);
    when(userPermissionRepository.upsert(expected)).thenReturn(expected);

    // act
    var result = service.createUserWithPermissions(provisioning);

    // assert
    assertEquals(expected, result);
    verify(userPermissionRepository).upsert(expected);

    // the password that was set in Keycloak has to be the one the user is mailed,
    // otherwise the account is provisioned but unusable
    var passwordToKeycloak = ArgumentCaptor.forClass(String.class);
    verify(keycloakUserRepository).create(eq(provisioning), passwordToKeycloak.capture());
    var passwordToUser = ArgumentCaptor.forClass(String.class);
    verify(accountNotificationService)
        .sendAccountCreatedNotification(eq(created), passwordToUser.capture());
    assertFalse(passwordToKeycloak.getValue().isBlank());
    assertEquals(passwordToKeycloak.getValue(), passwordToUser.getValue());
  }

  @Test
  void deletePermissions_delegatesToRepositoryAndLogsCurrentUser() {
    // arrange
    when(userService.getCurrentUserIdAndName()).thenReturn("admin-id Admin Name");

    // act
    service.deletePermissions("user-1");

    // assert
    verify(userPermissionRepository).deleteByUserId("user-1");
    verify(userService).getCurrentUserIdAndName();
  }
}
