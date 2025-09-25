package at.ac.tuwien.refop.domain.common;


import at.ac.tuwien.refop.domain.common.exceptions.InvalidRisIdException;

public record RisId(String memberId, ERisIdType idType, String id) {

    public RisId {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member ID must not be null or empty");
        }
        if (idType == null) {
            throw new IllegalArgumentException("ID type must not be null");
        }
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must not be null or empty");
        }
    }

    @Override
    public String toString() {
        return String.format("ris:%s:%s:%s", memberId.toUpperCase(), idType.toString().toLowerCase(), id);
    }

  /**
   * Creates a RisId from a string representation.
   * This method is intended to be used when being confident that the provided string is in the correct format, as it
   * will throw a runtime exception if the input is invalid.
   */
  public static RisId fromString(String risIdString) {
        String[] parts = risIdString.split(":");
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("ris")) {
            throw new IllegalArgumentException("Invalid RIS ID format, expected format 'ris:[memberId]:[idType]:[id]'");
        }
        try {
            ERisIdType idType = ERisIdType.valueOf(parts[2].toUpperCase());
            return new RisId(parts[1], idType, parts[3]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ID type in RIS ID format");
        }
    }

  /**
   * Similar to {@link #fromString(String)}, but throws a checked exception if the provided string is not in the correct format.
   * This method is intended to be used when parsing user input, as it explicitly throws an exception if the input is invalid.
   *
   * @throws InvalidRisIdException if the provided string is not in the correct format
   *         or contains invalid values
   */
  public static RisId parseString(String risIdString) throws InvalidRisIdException {
        String[] parts = risIdString.split(":");
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("ris")) {
            throw new InvalidRisIdException(risIdString);
        }
        try {
            ERisIdType idType = ERisIdType.valueOf(parts[2].toUpperCase());
            return new RisId(parts[1], idType, parts[3]);
        } catch (IllegalArgumentException e) {
          throw new InvalidRisIdException(risIdString);
        }
    }


    public static RisId generateFundingRisId(String memberId, String id) {
        return new RisId(memberId, ERisIdType.FUNDING, id);
    }

    public static RisId generateOrgunitRisId(String memberId, String id) {
        return new RisId(memberId, ERisIdType.ORGUNIT, id);
    }
}