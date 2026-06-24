package at.ac.tuwien.fundify.application.service.contact;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import at.ac.tuwien.fundify.application.port.in.registration.RegistrationRequestUseCase;
import at.ac.tuwien.fundify.application.port.out.ticketing.TicketingService;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.ticketing.TicketCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactSubmissionServiceTest {

  @Mock
  private TicketingService ticketingService;

  @Mock
  private RegistrationRequestUseCase registrationRequestUseCase;

  private ContactSubmissionService service;

  @BeforeEach
  void setUp() {
    service = new ContactSubmissionService(ticketingService, registrationRequestUseCase);
  }

  @Test
  void submit_registrationCategory_capturesRequestAndSkipsTicketing() throws FundifyException {
    // arrange
    var submission = new TicketCreate(
        "Jane Doe", "Account please", "registration", "jane@funder.org", "Add me", "Funder");

    // act
    service.submit(submission);

    // assert
    verify(registrationRequestUseCase).submit("Jane Doe", "jane@funder.org", "Funder", "Add me");
    verifyNoInteractions(ticketingService);
  }

  @Test
  void submit_registrationCategory_isCaseInsensitive() throws FundifyException {
    // arrange
    var submission = new TicketCreate(
        "Jane Doe", "Account please", "Registration", "jane@funder.org", "Add me", "Funder");

    // act
    service.submit(submission);

    // assert
    verify(registrationRequestUseCase).submit("Jane Doe", "jane@funder.org", "Funder", "Add me");
    verifyNoInteractions(ticketingService);
  }

  @Test
  void submit_nonRegistrationCategory_createsTicketAndSkipsRegistration() throws FundifyException {
    // arrange
    var submission = new TicketCreate(
        "John Doe", "Bug", "error", "john@x.org", "Something broke", null);

    // act
    service.submit(submission);

    // assert
    verify(ticketingService).createTicket(submission);
    verifyNoInteractions(registrationRequestUseCase);
  }
}
