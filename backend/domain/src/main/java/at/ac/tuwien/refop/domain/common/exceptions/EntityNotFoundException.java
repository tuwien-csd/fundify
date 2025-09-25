package at.ac.tuwien.refop.domain.common.exceptions;

public final class EntityNotFoundException extends FundifyException {
  private static final String message = "The requested entity was not found.";
  private static final String errorCode = "ENTITY_NOT_FOUND";

  public EntityNotFoundException() {
    super(message, errorCode);
  }

  public EntityNotFoundException(String entityName, String id) {
    super(String.format("The requested %s with ID %s was not found or you have no permission to view it.", entityName, id), errorCode);
  }
  public static EntityNotFoundException callNotFound(String id) {
    return new EntityNotFoundException("Call", id);
  }
  public static EntityNotFoundException universityNotFound(String id) {
    return new EntityNotFoundException("University", id);
  }
  public static EntityNotFoundException annotatedCallNotFound(String universityId, String callInternalId) {
    return new EntityNotFoundException("Annotated Call", String.format("University ID %s, callInternalId%s", universityId, callInternalId));
  }
  public static EntityNotFoundException annotatedCallNotFound(String annotatedCallId) {
    return new EntityNotFoundException("Annotated Call", annotatedCallId);
  }
  public static EntityNotFoundException funderNotFound(String funderId) {
    return new EntityNotFoundException("Funder", funderId);
  }
  public static EntityNotFoundException programNotFound(String programId) {
    return new EntityNotFoundException("Program", programId);
  }
  public static EntityNotFoundException vocabularyNotFound(String vocabularyId) {
    return new EntityNotFoundException("Vocabulary", vocabularyId);
  }
}