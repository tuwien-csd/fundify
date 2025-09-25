package at.ac.tuwien.refop.adapters.in.rest.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

/**
 * This exception mapper should catch all unchecked exceptions. In general, you should try to make
 * your exceptions catchable by the other exception mappers.
 */
@Provider
@JBossLog
public class BaseExceptionMapper implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception exception) {
    switch (exception) {
      case WebApplicationException e -> {
        // Pass Quarkus WebApplicationException through as-is
        return Response.status(e.getResponse().getStatus())
            .entity(e.getResponse().getEntity())
            .build();
      }
      default -> {
        log.error("Unhandled exception", exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("An internal error occurred while processing the request.").build();

      }
    }
  }
}