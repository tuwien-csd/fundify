package at.ac.tuwien.refop.domain.common.exceptions;

import java.util.Map;
import java.util.UUID;

/**
 * An abstract base exception for all exceptions in the Fundify application. It provides a common
 * structure for error messages and error codes. This class is sealed to restrict its subclasses to
 * specific exceptions, which allows for exhaustive matching in switch statements in the
 * ExceptionMappers.
 */
public abstract sealed class FundifyException extends RuntimeException permits CallBaseException,
    ProgramBaseException, NoUserPermissionsConfiguredException,
    EntityNotFoundException, UnexpectedErrorException, InvalidRisIdException,
    RisIdMustNotBeSetException, ExternalApiErrorException, AnnotatedCallBaseException,
    InsufficientPermissionsException {

  private final String message;
  private final String errorCode;
  private final UUID correlationId = UUID.randomUUID();

  public FundifyException(String message) {
    super(message);
    this.message = message;
    this.errorCode = "FUNDIFY_UNKNOWN_ERROR";
  }

  public FundifyException(String message, String errorCode) {
    super(message);
    this.message = message;
    this.errorCode = errorCode;
  }

  public String toJson() {
    return String.format("{\"error\": \"%s\", \"code\": \"%s\", \"correlationId\": \"%s\"}", message, errorCode, correlationId);
  }

  /**
   *  Returns structured fields for server-side logging/observability.
   */
  public Map<String, String> getLogContext() {
    String causeType = getCause() != null ? getCause().getClass().getSimpleName() : "none";
    return Map.of(
        "type", getClass().getSimpleName(),
        "errorCode", errorCode,
        "correlationId", correlationId.toString(),
        "message", message,
        "causeType", causeType
    );
  }
}
