package at.ac.tuwien.fundify.application.port.out.ticketing;

import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.ticketing.AppendableTicketCreate;
import at.ac.tuwien.fundify.domain.ticketing.TicketCreate;

public interface TicketingService {

  void createTicket(TicketCreate createInput) throws FundifyException;

  void createOrAppend(AppendableTicketCreate input);

}
