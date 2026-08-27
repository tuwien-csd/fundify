package at.ac.tuwien.fundify.adapters.out.fundify;

import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private final Mailer mailer = mock(Mailer.class);
    private final EmailFactory emailFactory = mock(EmailFactory.class);
    private final EmailService emailService = new EmailService(mailer, emailFactory);

    private static final KeycloakUser NEW_USER = new KeycloakUser(
            "kc-1", "jane@univie.ac.at", "jane@univie.ac.at", "Jane", "Doe", true);

    EmailServiceTest() {
        // normally injected via @ConfigProperty
        emailService.appUrl = "https://fundify.example.org";
    }

    @Test
    void sendSyncErrorNotification_delegatesToFactoryAndMailer() {
        String memberId = "fwf";
        String contactEmail = "contact@fwf.ac.at";
        List<String> errors = List.of("Error A", "Error B");
        Mail expectedMail = mock(Mail.class);

        given(emailFactory.createSyncErrorEmail(memberId, contactEmail, errors)).willReturn(expectedMail);

        emailService.sendSyncErrorNotification(memberId, contactEmail, errors);

        verify(emailFactory).createSyncErrorEmail(memberId, contactEmail, errors);
        verify(mailer).send(expectedMail);
        verify(mailer, times(1)).send(any(Mail.class));
    }

    @Test
    void sendAccountCreatedNotification_delegatesToFactoryAndMailer() {
        Mail expectedMail = mock(Mail.class);
        given(emailFactory.createAccountCreatedEmail(NEW_USER, "temp-pw", "https://fundify.example.org"))
                .willReturn(expectedMail);

        emailService.sendAccountCreatedNotification(NEW_USER, "temp-pw");

        verify(emailFactory)
                .createAccountCreatedEmail(NEW_USER, "temp-pw", "https://fundify.example.org");
        verify(mailer).send(expectedMail);
    }

    @Test
    void sendAccountCreatedNotification_swallowsMailerFailure() {
        // the Keycloak account already exists at this point, so a failing mail must not
        // fail the request that provisioned it
        Mail expectedMail = mock(Mail.class);
        given(emailFactory.createAccountCreatedEmail(any(), anyString(), anyString()))
                .willReturn(expectedMail);
        doThrow(new IllegalStateException("SMTP down")).when(mailer).send(expectedMail);

        Assertions.assertDoesNotThrow(
                () -> emailService.sendAccountCreatedNotification(NEW_USER, "temp-pw"));
    }

}
