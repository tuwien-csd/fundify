package at.ac.tuwien.fundify.domain.ticketing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TicketCreateTest {

  private static TicketCreate withName(String firstName, String lastName) {
    return new TicketCreate(firstName, lastName, "subject", "other", "a@b.org", "message", null);
  }

  @Test
  void fullName_joinsGivenNameAndSurname() {
    assertEquals("Jane Doe", withName("Jane", "Doe").fullName());
  }

  @Test
  void fullName_trimsAndSkipsBlankParts() {
    assertEquals("Jane", withName(" Jane ", "  ").fullName());
    assertEquals("Doe", withName(null, "Doe").fullName());
  }

  @Test
  void fullName_isNullWhenNothingIsGiven() {
    // the Jira issue body skips blank values, so a missing name must not add an empty row
    assertNull(withName(null, null).fullName());
    assertNull(withName("", " ").fullName());
  }
}
