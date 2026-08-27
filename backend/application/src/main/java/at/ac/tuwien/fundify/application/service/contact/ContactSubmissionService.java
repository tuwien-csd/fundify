package at.ac.tuwien.fundify.application.service.contact;

import at.ac.tuwien.fundify.application.port.in.contact.ContactSubmissionUseCase;
import at.ac.tuwien.fundify.application.port.in.registration.RegistrationRequestUseCase;
import at.ac.tuwien.fundify.application.port.out.ticketing.TicketingService;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.ticketing.TicketCreate;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
public class ContactSubmissionService implements ContactSubmissionUseCase {

  /** Contact-form category that represents a request for a Fundify account. */
  private static final String REGISTRATION_CATEGORY = "registration";

  private final TicketingService ticketingService;
  private final RegistrationRequestUseCase registrationRequestUseCase;

  @Override
  public void submit(TicketCreate submission) throws FundifyException {
    if (isRegistration(submission)) {
      registrationRequestUseCase.submit(
          submission.firstName(),
          submission.lastName(),
          submission.email(),
          submission.kindOfInstitution(),
          submission.message());
    }
    ticketingService.createTicket(submission);
  }

  private boolean isRegistration(TicketCreate submission) {
    return REGISTRATION_CATEGORY.equalsIgnoreCase(submission.category());
  }
}
