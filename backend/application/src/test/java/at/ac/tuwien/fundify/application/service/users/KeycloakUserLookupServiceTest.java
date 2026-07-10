package at.ac.tuwien.fundify.application.service.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.application.port.out.keycloak.KeycloakUserRepository;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KeycloakUserLookupServiceTest {

  @Mock
  private KeycloakUserRepository keycloakUserRepository;

  private KeycloakUserLookupService service;

  @BeforeEach
  void setUp() {
    service = new KeycloakUserLookupService(keycloakUserRepository);
  }

  @Test
  void getAllUsers_delegatesToRepositoryAndReturnsResult() {
    // arrange
    List<KeycloakUser> users = List.of(
        new KeycloakUser("id-1", "alice", "alice@example.com", "Alice", "A", true),
        new KeycloakUser("id-2", "bob", "bob@example.com", "Bob", "B", false));
    when(keycloakUserRepository.findAll()).thenReturn(users);

    // act
    List<KeycloakUser> result = service.getAllUsers();

    // assert
    assertEquals(users, result);
    verify(keycloakUserRepository).findAll();
  }

  @Test
  void getUserById_whenFound_returnsUser() {
    // arrange
    KeycloakUser user = new KeycloakUser("id-1", "alice", "alice@example.com", "Alice", "A", true);
    when(keycloakUserRepository.findById("id-1")).thenReturn(Optional.of(user));

    // act
    Optional<KeycloakUser> result = service.getUserById("id-1");

    // assert
    assertTrue(result.isPresent());
    assertEquals(user, result.get());
    verify(keycloakUserRepository).findById("id-1");
  }

  @Test
  void getUserById_whenMissing_returnsEmpty() {
    // arrange
    when(keycloakUserRepository.findById("missing")).thenReturn(Optional.empty());

    // act
    Optional<KeycloakUser> result = service.getUserById("missing");

    // assert
    assertTrue(result.isEmpty());
    verify(keycloakUserRepository).findById("missing");
  }
}
