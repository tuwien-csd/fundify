package at.ac.tuwien.fundify.adapters.in.rest.resources;

import at.ac.tuwien.fundify.application.port.in.registration.RegistrationRequestUseCase;
import at.ac.tuwien.fundify.domain.common.UserRole;
import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@Path("/api/registration-requests")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@RequiredArgsConstructor
public class RegistrationRequestResource {

  private final RegistrationRequestUseCase registrationRequestUseCase;

  @GET
  @RolesAllowed(UserRole.Names.ADMIN)
  public List<RegistrationRequest> getAll() {
    return registrationRequestUseCase.getAll();
  }

  @DELETE
  @Path("/{id}")
  @RolesAllowed(UserRole.Names.ADMIN)
  public void delete(@PathParam("id") String id) {
    registrationRequestUseCase.delete(id);
  }
}
