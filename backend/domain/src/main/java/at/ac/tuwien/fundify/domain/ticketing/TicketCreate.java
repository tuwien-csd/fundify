package at.ac.tuwien.fundify.domain.ticketing;

import java.util.stream.Stream;

public record TicketCreate(
    String firstName,
    String lastName,
    String subject,
    String category,
    String email,
    String message,
    String kindOfInstitution
) {

  /**
   * Given name and surname joined for display in contexts that show a single name,
   * such as the Jira issue body. Blank parts are skipped.
   */
  public String fullName() {
    var joined = Stream.of(firstName, lastName)
        .filter(part -> part != null && !part.isBlank())
        .map(String::trim)
        .reduce((first, second) -> first + " " + second)
        .orElse("");
    return joined.isEmpty() ? null : joined;
  }
}
