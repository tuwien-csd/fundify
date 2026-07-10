package at.ac.tuwien.fundify.adapters.out.fundify;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private final Mailer mailer = mock(Mailer.class);
    private final EmailFactory emailFactory = mock(EmailFactory.class);
    private final EmailService emailService = new EmailService(mailer, emailFactory);

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

}
