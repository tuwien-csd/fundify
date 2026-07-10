package at.ac.tuwien.fundify.application.service.registration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import at.ac.tuwien.fundify.application.port.out.persistence.RegistrationRequestRepository;
import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationRequestServiceTest {

  @Mock
  private RegistrationRequestRepository registrationRequestRepository;

  private RegistrationRequestService service;

  @BeforeEach
  void setUp() {
    service = new RegistrationRequestService(registrationRequestRepository);
  }

  @Test
  void submit_persistsRequestWithGeneratedIdAndTimestamp() {
    // arrange
    when(registrationRequestRepository.save(org.mockito.ArgumentMatchers.any()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // act
    var result = service.submit("Jane Doe", "jane@funder.org", "Funder", "Please add me");

    // assert
    ArgumentCaptor<RegistrationRequest> captor =
        ArgumentCaptor.forClass(RegistrationRequest.class);
    verify(registrationRequestRepository).save(captor.capture());
    var saved = captor.getValue();
    assertNotNull(saved.id());
    assertNotNull(saved.createdAt());
    assertEquals("Jane Doe", saved.name());
    assertEquals("jane@funder.org", saved.email());
    assertEquals("Funder", saved.kindOfInstitution());
    assertEquals("Please add me", saved.message());
    assertEquals(saved, result);
  }

  @Test
  void getAll_delegatesToRepository() {
    // arrange
    List<RegistrationRequest> requests = List.of(
        new RegistrationRequest("id-1", "A", "a@x.org", "Funder", "msg", java.time.Instant.now()));
    when(registrationRequestRepository.findAllRequests()).thenReturn(requests);

    // act
    var result = service.getAll();

    // assert
    assertEquals(requests, result);
    verify(registrationRequestRepository).findAllRequests();
  }

  @Test
  void delete_delegatesToRepository() {
    // act
    service.delete("id-1");

    // assert
    verify(registrationRequestRepository).deleteById("id-1");
  }
}
