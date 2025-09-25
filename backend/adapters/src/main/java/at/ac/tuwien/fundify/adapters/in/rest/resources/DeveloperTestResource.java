package at.ac.tuwien.fundify.adapters.in.rest.resources;

import at.ac.tuwien.fundify.domain.common.UserRole;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * DeveloperTestResource provides a REST endpoint for testing purposes in a development environment.
 * This endpoint is enabled only when the `fundify.development.enableDevController` build property
 * is set to `true`.
 *
 *
 */
@Path("/api/developertest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
@RolesAllowed(UserRole.Names.ADMIN)
public class DeveloperTestResource {

  @ConfigProperty(name = "fundify.development.enableDevController", defaultValue = "false")
  boolean isDevControllerEnabled;

  @GET
  public String throwException() {
    if (!isDevControllerEnabled) {
      throw new NotFoundException();
    }
    throw new RuntimeException("Exception from DeveloperTestResource to test alerting");
  }

}