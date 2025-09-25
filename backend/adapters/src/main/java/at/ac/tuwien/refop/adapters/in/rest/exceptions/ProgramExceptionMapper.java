package at.ac.tuwien.refop.adapters.in.rest.exceptions;

import at.ac.tuwien.refop.domain.common.exceptions.ProgramAlreadyPublishedException;
import at.ac.tuwien.refop.domain.common.exceptions.ProgramBaseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

@Provider
@JBossLog
public class ProgramExceptionMapper implements ExceptionMapper<ProgramBaseException> {

  @Override
  public Response toResponse(ProgramBaseException exception) {
    switch (exception) {
      case ProgramAlreadyPublishedException e -> {
        log.info(e.getLogContext());
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(e.toJson()  )
            .build();
      }
    }
  }
}