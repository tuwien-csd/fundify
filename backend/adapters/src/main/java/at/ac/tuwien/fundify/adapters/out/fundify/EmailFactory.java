package at.ac.tuwien.fundify.adapters.out.fundify;

import at.ac.tuwien.fundify.domain.common.FundifyUser;
import at.ac.tuwien.fundify.domain.funding.Call;
import io.quarkus.mailer.Mail;
import jakarta.enterprise.context.ApplicationScoped;

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

}
