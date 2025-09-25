package at.ac.tuwien.refop.adapters.in.rest.exceptions;

import at.ac.tuwien.refop.domain.common.exceptions.CallAlreadyPublishedException;
import at.ac.tuwien.refop.domain.common.exceptions.CallBaseException;
import at.ac.tuwien.refop.domain.common.exceptions.CallRisIdIsSetException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

@Provider
@JBossLog
public class CallExceptionMapper implements ExceptionMapper<CallBaseException> {

  @Override
  public Response toResponse(CallBaseException exception) {
    switch (exception) {
      case CallAlreadyPublishedException e -> {
        log.info("Call already published", exception);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(e.toJson()  )
            .build();
      }
      case CallRisIdIsSetException e -> {
        log.info(e.getLogContext());
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(e.toJson())
            .build();
      }
    }
  }
}