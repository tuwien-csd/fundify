package at.ac.tuwien.fundify.adapters.out.fundify;

import at.ac.tuwien.fundify.domain.common.FundifyUser;
import at.ac.tuwien.fundify.domain.common.KeycloakUser;
import at.ac.tuwien.fundify.domain.funding.Call;
import io.quarkus.mailer.Mail;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.text.StringEscapeUtils;

@ApplicationScoped
public class EmailFactory {

  public Mail createNotificationEmail(FundifyUser subscriber, Call call) {

    String subject = String.format("Update: The Call '%s' has been modified", call.getName().getFirst().text());
    String body = String.format(
        """
            <html>
            <body>
                <p>Hello %s,</p>
            
                <p>The Call you are subscribed to, '<b>%s</b>', has been updated.</p>
            
                <p>If you no longer wish to receive updates for this Call, follow these steps:</p>
            
                <ol>
                  <li>Go to the <b>Dashboard</b> and select the 'Calls' tab.</li>
                  <li>Find the Call in the list.</li>
                  <li>Click the <b>action menu</b> (the three dots) next to the Call.</li>
                  <li>Select the 'Unsubscribe' option.</li>
                </ol>
            
                <p>Kind regards,<br>
                Your Fundify Team</p>
            </body>
            </html>""",
        subscriber.name(), call.getName().getFirst().text()
    );

    return Mail.withHtml(subscriber.email(), subject, body);
  }

  public Mail createSyncErrorEmail(String memberId, String contactEmail, List<String> errorDetails) {
    String subject = String.format("[Fundify Sync Error] Error on importing data for '%s'", memberId.toUpperCase());
    String detailsHtml = errorDetails.stream()
        .map(d -> "<li>" + d + "</li>")
        .collect(Collectors.joining());
    String body = String.format(
        """
            <html>
            <body>
                <p>Dear %s,</p>

                <p>An error occurred while processing the RIS funding data.</p>

                <p>Error details:</p>
                <ul>
                    %s
                </ul>

                <p>Please review the data format and contact the Fundify team if you need assistance.</p>

                <p>Kind regards,<br>
                Your Fundify Team</p>
            </body>
            </html>""",
        memberId.toUpperCase(), detailsHtml
    );

    return Mail.withHtml(contactEmail, subject, body);
  }

  /**
   * Welcomes a newly provisioned user and hands them their first-login password.
   * Provisioned accounts have no credential otherwise, so this mail is the only way
   * in; Keycloak forces the password to be replaced immediately after it is used.
   */
  public Mail createAccountCreatedEmail(KeycloakUser user, String temporaryPassword, String appUrl) {
    String subject = "Your Fundify account has been created";
    String greetingName = escapeHtml(fullName(user));
    String body = String.format(
        """
            <html>
            <body>
                <p>Hello %s,</p>

                <p>An account has been created for you in Fundify.</p>

                <p>Use the following credentials for your first login:</p>

                <ul>
                    <li>Email: <b>%s</b></li>
                    <li>Temporary password: <code>%s</code></li>
                </ul>

                <p>You will be asked to choose your own password right after logging in.
                The temporary password above cannot be used afterwards.</p>

                <p>You can reach Fundify at <a href="%s">%s</a>.</p>

                <p>Kind regards,<br>
                Your Fundify Team</p>
            </body>
            </html>""",
        greetingName, escapeHtml(user.email()), escapeHtml(temporaryPassword), appUrl, appUrl
    );

    return Mail.withHtml(user.email(), subject, body);
  }

  private String fullName(KeycloakUser user) {
    var joined = Stream.of(user.firstName(), user.lastName())
        .filter(part -> part != null && !part.isBlank())
        .map(String::trim)
        .collect(Collectors.joining(" "));
    // fall back to the address so the greeting is never left dangling
    return joined.isEmpty() ? user.email() : joined;
  }

  /**
   * Given names and surnames reach us from the public contact form, so they are
   * untrusted input being interpolated into an HTML body. escapeHtml4 also encodes
   * non-ASCII characters as entities, which keeps umlauts in names intact regardless
   * of how the receiving client interprets the body's charset.
   */
  private String escapeHtml(String value) {
    return value == null ? "" : StringEscapeUtils.escapeHtml4(value);
  }

}
