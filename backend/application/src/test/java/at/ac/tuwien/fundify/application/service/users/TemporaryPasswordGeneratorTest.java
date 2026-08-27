package at.ac.tuwien.fundify.application.service.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class TemporaryPasswordGeneratorTest {

  private static final int SAMPLES = 200;

  private final TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();

  @Test
  void generate_returnsAPasswordOfTheExpectedLength() {
    assertEquals(24, generator.generate().length());
  }

  @Test
  void generate_alwaysCoversEveryCharacterClass() {
    // the realm used in staging and production may enforce a password policy
    for (int i = 0; i < SAMPLES; i++) {
      var password = generator.generate();

      assertTrue(password.matches(".*[a-z].*"), () -> "no lower-case letter in " + password);
      assertTrue(password.matches(".*[A-Z].*"), () -> "no upper-case letter in " + password);
      assertTrue(password.matches(".*[0-9].*"), () -> "no digit in " + password);
      assertTrue(password.matches(".*[!#$%&*+\\-=?@^_].*"),
          () -> "no special character in " + password);
    }
  }

  @Test
  void generate_omitsCharactersThatWouldNeedEscaping() {
    var passwords = IntStream.range(0, SAMPLES)
        .mapToObj(i -> generator.generate())
        .collect(Collectors.joining());

    // quotes, angle brackets, backslashes and whitespace would make the value
    // awkward to embed in HTML, JSON or a shell command
    assertTrue(passwords.matches("[a-zA-Z0-9!#$%&*+\\-=?@^_]+"),
        "generated passwords contain a character outside the intended alphabet");
  }

  @Test
  void generate_doesNotPinTheRequiredClassesToFixedPositions() {
    // without the shuffle every password would start lower, upper, digit, special
    var firstCharacters = IntStream.range(0, SAMPLES)
        .mapToObj(i -> generator.generate().substring(0, 1))
        .collect(Collectors.toSet());

    assertTrue(firstCharacters.size() > 1, "the first character never varies");
  }

  @Test
  void generate_returnsADifferentPasswordEachTime() {
    var passwords = IntStream.range(0, SAMPLES)
        .mapToObj(i -> generator.generate())
        .collect(Collectors.toSet());

    assertEquals(SAMPLES, passwords.size());
  }
}
