package at.ac.tuwien.fundify.application.service.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.out.persistence.UserPermissionRepository;
import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;
import at.ac.tuwien.fundify.domain.common.UserRole;
import java.util.List;
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
  private UserService userService;

  private UserPermissionManagementService service;

  @BeforeEach
  void setUp() {
    service = new UserPermissionManagementService(userPermissionRepository, userService);
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
