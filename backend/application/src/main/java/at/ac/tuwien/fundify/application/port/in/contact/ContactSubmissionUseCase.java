package at.ac.tuwien.fundify.application.port.in.contact;

import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.ticketing.TicketCreate;

public interface ContactSubmissionUseCase {

  /**
   * Handles a public contact-form submission. Registration requests are captured
   * inside Fundify for an admin to act on; all other categories are filed as
   * tickets in the external ticketing system.
   */
  void submit(TicketCreate submission) throws FundifyException;
}
