package at.ac.tuwien.refop.adapters.in.rest.exceptions;

import at.ac.tuwien.refop.domain.common.exceptions.AnnotatedCallBaseException;
import at.ac.tuwien.refop.domain.common.exceptions.CallBaseException;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.common.exceptions.ExternalApiErrorException;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import at.ac.tuwien.refop.domain.common.exceptions.InsufficientPermissionsException;
import at.ac.tuwien.refop.domain.common.exceptions.InvalidRisIdException;
import at.ac.tuwien.refop.domain.common.exceptions.NoUserPermissionsConfiguredException;
import at.ac.tuwien.refop.domain.common.exceptions.ProgramBaseException;
import at.ac.tuwien.refop.domain.common.exceptions.RisIdMustNotBeSetException;
import at.ac.tuwien.refop.domain.common.exceptions.UnexpectedErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;

/**
 * This exception mapper handles exceptions that are not specific to a particular domain. It is used
 * to catch base exceptions and provide appropriate responses.
 */
@Provider
@JBossLog
public class FundifyExceptionMapper implements ExceptionMapper<FundifyException> {

  private static final String EXCEPTION_SHOULD_BE_HANDLED_ELSEWHERE_MESSAGE = "FundifyException Mapper caught this exception, although it should have been handled by a more specific ExceptionMapper.";

  @Override
  public Response toResponse(FundifyException exception) {
    switch (exception) {
      case EntityNotFoundException ignored -> {
        log.info(exception.getLogContext());
        return Response.status(Response.Status.NOT_FOUND)
            .entity(exception.toJson())
            .build();
      }
      case UnexpectedErrorException e -> {
        log.error(e.getLogContext() , e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(e.toJson())
            .build();
      }
      case RisIdMustNotBeSetException e -> {
        log.info(e.getLogContext());
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(e.toJson())
            .build();
      }
      case InvalidRisIdException e -> {
        log.info(e.getLogContext());
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(e.toJson())
            .build();
      }
      case ExternalApiErrorException e -> {
        log.error(e.getLogContext());
        // Return client errors (4xx) as is, but map server errors (5xx) to INTERNAL_SERVER_ERROR
        if (e.externalApiStatusCode >= 500) {
          log.error("External API returned a server error: " + e.externalApiStatusCode, e);
          return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity(e.toJson())
              .build();
        }
        return Response.status(e.externalApiStatusCode)
            .entity(new UnexpectedErrorException().toJson())
            .build();
      }
      case InsufficientPermissionsException e -> {
        return Response.status(Status.FORBIDDEN)
            .entity(e.toJson())
            .build();
      }
      case NoUserPermissionsConfiguredException e -> {
        return Response.status(Status.FORBIDDEN)
            .entity(e.toJson())
            .build();
      }
      //Below exceptions should have been handled by more specific exception mappers and never occur here
      case CallBaseException e -> {
        log.error(
            EXCEPTION_SHOULD_BE_HANDLED_ELSEWHERE_MESSAGE,
            e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
      case ProgramBaseException e -> {
        log.error(
            EXCEPTION_SHOULD_BE_HANDLED_ELSEWHERE_MESSAGE,
            e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
      case AnnotatedCallBaseException e -> {
        log.error(
            EXCEPTION_SHOULD_BE_HANDLED_ELSEWHERE_MESSAGE,
            e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
    }
  }
}