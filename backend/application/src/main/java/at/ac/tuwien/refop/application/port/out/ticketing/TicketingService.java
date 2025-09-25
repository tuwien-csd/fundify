package at.ac.tuwien.refop.application.port.out.ticketing;

import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import at.ac.tuwien.refop.domain.ticketing.AppendableTicketCreate;
import at.ac.tuwien.refop.domain.ticketing.TicketCreate;

public interface TicketingService {

  void createTicket(TicketCreate createInput) throws FundifyException;

  void createOrAppend(AppendableTicketCreate input);

}
