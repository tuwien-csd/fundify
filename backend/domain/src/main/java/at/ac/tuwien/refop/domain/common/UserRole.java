package at.ac.tuwien.refop.domain.common;

/**
 * Enum representation of valid user roles.
 *
 * The enum names are intentionally uppercase to match common YAML inputs like "FUNDER".
 * The string value (toString) returns the canonical role string as defined in UserRole constants.
 */
public enum UserRole {
  ADMIN(Names.ADMIN),
  FUNDER(Names.FUNDER),
  ANNOTATOR(Names.ANNOTATOR),
  EXTERNAL_API_CLIENT(Names.EXTERNAL_API_CLIENT);

  private final String value;

  UserRole(String value) {
    this.value = value;
  }

  public static class Names {
    public static final String ADMIN = "admin";
    public static final String FUNDER = "funder";
    public static final String ANNOTATOR = "annotator";
    public static final String EXTERNAL_API_CLIENT = "external_api_client";
  }

  @Override
  public String toString() {
    return value;
  }
}
