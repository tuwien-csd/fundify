package at.ac.tuwien.fundify.adapters.out.fundify;

import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import io.quarkus.mailer.Mail;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailFactoryTest {

    private final EmailFactory emailFactory = new EmailFactory();

    @Test
    void createSyncErrorEmail_setsCorrectRecipientAndSubject() {
        Mail mail = emailFactory.createSyncErrorEmail("fwf", "contact@fwf.ac.at", List.of("Error A"));

        assertEquals(List.of("contact@fwf.ac.at"), mail.getTo());
        assertEquals("[Fundify Sync Error] Error on importing data for 'FWF'", mail.getSubject());
    }

    @Test
    void createSyncErrorEmail_includesAllErrorDetailsAsListItems() {
        List<String> errors = List.of("Parse failure at line 10", "Missing required field: title");

        Mail mail = emailFactory.createSyncErrorEmail("fwf", "contact@fwf.ac.at", errors);

        assertTrue(mail.getHtml().contains("<li>Parse failure at line 10</li>"));
        assertTrue(mail.getHtml().contains("<li>Missing required field: title</li>"));
    }

    @Test
    void createSyncErrorEmail_handlesEmptyErrorList() {
        Mail mail = emailFactory.createSyncErrorEmail("fwf", "contact@fwf.ac.at", List.of());

        assertNotNull(mail);
        assertEquals(List.of("contact@fwf.ac.at"), mail.getTo());
    }

    private static final String APP_URL = "https://fundify.example.org";

    private static KeycloakUser user(String firstName, String lastName) {
        return new KeycloakUser("kc-1", "jane@univie.ac.at", "jane@univie.ac.at",
                firstName, lastName, true);
    }

    @Test
    void createAccountCreatedEmail_setsRecipientAndSubject() {
        Mail mail = emailFactory.createAccountCreatedEmail(user("Jane", "Doe"), "7Kq2m-Vt9Rx-4wPbn", APP_URL);

        assertEquals(List.of("jane@univie.ac.at"), mail.getTo());
        assertEquals("Your Fundify account has been created", mail.getSubject());
    }

    @Test
    void createAccountCreatedEmail_includesNameCredentialsAndAppUrl() {
        Mail mail = emailFactory.createAccountCreatedEmail(user("Jane", "Doe"), "7Kq2m-Vt9Rx-4wPbn", APP_URL);

        assertTrue(mail.getHtml().contains("Hello Jane Doe,"));
        assertTrue(mail.getHtml().contains("jane@univie.ac.at"));
        assertTrue(mail.getHtml().contains("7Kq2m-Vt9Rx-4wPbn"));
        assertTrue(mail.getHtml().contains(APP_URL));
    }

    @Test
    void createAccountCreatedEmail_fallsBackToTheEmailWhenNoNameIsKnown() {
        // accounts provisioned before names were captured have neither part set
        Mail mail = emailFactory.createAccountCreatedEmail(user(null, null), "temp-pw", APP_URL);

        assertTrue(mail.getHtml().contains("Hello jane@univie.ac.at,"));
    }

    @Test
    void createAccountCreatedEmail_encodesNonAsciiNamesAsEntities() {
        // umlauts are common in the names this reaches us with, and entity-encoding them
        // makes the greeting independent of how the client reads the body's charset
        Mail mail = emailFactory.createAccountCreatedEmail(
                user("J\u00fcrgen", "Schr\u00f6dinger"), "temp-pw", APP_URL);

        assertTrue(mail.getHtml().contains("Hello J&uuml;rgen Schr&ouml;dinger,"));
    }

    @Test
    void createAccountCreatedEmail_escapesTheGeneratedPassword() {
        // '&' is part of the generated alphabet, so it has to survive the HTML body as
        // an entity; a mail client renders it back to the character the user needs
        Mail mail = emailFactory.createAccountCreatedEmail(
                user("Jane", "Doe"), "a&b<c", APP_URL);

        assertTrue(mail.getHtml().contains("<code>a&amp;b&lt;c</code>"));
    }

    @Test
    void createAccountCreatedEmail_escapesTheName() {
        // names arrive from the public, unauthenticated contact form
        Mail mail = emailFactory.createAccountCreatedEmail(
                user("<b>Jane", "Doe</b>"), "temp-pw", APP_URL);

        assertFalse(mail.getHtml().contains("<b>Jane"));
        assertTrue(mail.getHtml().contains("&lt;b&gt;Jane Doe&lt;/b&gt;"));
    }

}
