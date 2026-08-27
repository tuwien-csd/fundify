package at.ac.tuwien.fundify.application.service.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
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

  private UserPermissionManagementService service;

  @BeforeEach
  void setUp() {
    service = new UserPermissionManagementService(userPermissionRepository,
        keycloakUserRepository, userService);
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
    // an existing account is reused as-is: its name is deliberately not overwritten
    verify(keycloakUserRepository, never()).create(any());
  }

  @Test
  void createUserWithPermissions_unknownUser_provisionsKeycloakUserFirst() {
    // arrange
    var affiliationId = "univie";
    var roles = List.of(UserRole.FUNDER);
    var provisioning = new UserProvisioning(
        "new@univie.ac.at", "New", "User", roles, affiliationId);
    when(keycloakUserRepository.findByEmail("new@univie.ac.at")).thenReturn(Optional.empty());
    when(keycloakUserRepository.create(provisioning))
        .thenReturn(new KeycloakUser("new-id", "new@univie.ac.at", "new@univie.ac.at", "New", "User",
            true));
    var expected = new UserPermissionHolder("new-id", roles, affiliationId);
    when(userPermissionRepository.upsert(expected)).thenReturn(expected);

    // act
    var result = service.createUserWithPermissions(provisioning);

    // assert
    assertEquals(expected, result);
    verify(keycloakUserRepository).create(provisioning);
    verify(userPermissionRepository).upsert(expected);
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
