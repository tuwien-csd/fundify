package at.ac.tuwien.fundify.adapters.in.rest.resources;

import at.ac.tuwien.fundify.adapters.in.rest.dto.TicketCreateWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.mapper.TicketWebModelMapper;
import at.ac.tuwien.fundify.application.port.out.ticketing.TicketingService;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@Path("/api/contact")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
public class ContactResource {

  private final TicketingService ticketingService;

  @POST
  public void create(TicketCreateWebModel createInput) throws FundifyException {
    log.info("Creating a ticket for user with email: " + createInput.email());
    this.ticketingService.createTicket(TicketWebModelMapper.INSTANCE.toDomain(createInput));
  }

}