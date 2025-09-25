package at.ac.tuwien.refop.adapters.in.rest.exceptions;

import at.ac.tuwien.refop.domain.common.exceptions.AnnotatedCallAlreadyAnnotatedException;
import at.ac.tuwien.refop.domain.common.exceptions.AnnotatedCallBaseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

@Provider
@JBossLog
public class AnnotatedCallExceptionMapper implements ExceptionMapper<AnnotatedCallBaseException> {

  @Override
  public Response toResponse(AnnotatedCallBaseException exception) {
    switch (exception) {
      case AnnotatedCallAlreadyAnnotatedException e -> {
        log.info(e.getLogContext());
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(e.toJson()  )
            .build();
      }
    }
  }
}