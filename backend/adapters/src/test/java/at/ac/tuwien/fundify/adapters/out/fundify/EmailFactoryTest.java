package at.ac.tuwien.fundify.adapters.out.fundify;

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
}
