package at.ac.tuwien.fundify.application.service.users;

import jakarta.enterprise.context.ApplicationScoped;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates the one-off password a newly provisioned user receives by email. The
 * password is meant to be copied out of that mail rather than retyped, so it is
 * optimised for strength instead of legibility.
 *
 * <p>It always contains at least one lower-case letter, one upper-case letter, one
 * digit and one special character, so it satisfies a strict Keycloak password
 * policy. The dev realm has none configured, but the realm used in staging and
 * production is managed outside this repository and may well have one.
 */
@ApplicationScoped
public class TemporaryPasswordGenerator {

  private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String DIGITS = "0123456789";
  /** Quotes, angle brackets and backslashes are left out so the value stays safe to
   * embed in HTML, JSON and shell contexts without depending on escaping. */
  private static final String SPECIAL = "!#$%&*+-=?@^_";

  private static final List<String> REQUIRED_GROUPS = List.of(LOWER, UPPER, DIGITS, SPECIAL);
  private static final String ALPHABET = LOWER + UPPER + DIGITS + SPECIAL;
  private static final int LENGTH = 24;

  private final SecureRandom random = new SecureRandom();

  public String generate() {
    var characters = new ArrayList<Character>(LENGTH);

    // guarantee the character classes a password policy may require
    for (var group : REQUIRED_GROUPS) {
      characters.add(randomCharacter(group));
    }
    while (characters.size() < LENGTH) {
      characters.add(randomCharacter(ALPHABET));
    }

    shuffle(characters);

    var password = new StringBuilder(LENGTH);
    characters.forEach(password::append);
    return password.toString();
  }

  private char randomCharacter(String source) {
    return source.charAt(random.nextInt(source.length()));
  }

  /**
   * Fisher-Yates, so the guaranteed characters above do not always land in the same
   * positions.
   */
  private void shuffle(List<Character> characters) {
    for (int i = characters.size() - 1; i > 0; i--) {
      int j = random.nextInt(i + 1);
      var swap = characters.get(i);
      characters.set(i, characters.get(j));
      characters.set(j, swap);
    }
  }
}
