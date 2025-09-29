package at.ac.tuwien.fundify.adapters.out.fundify;

import at.ac.tuwien.fundify.application.port.out.notification.NotificationService;
import at.ac.tuwien.fundify.domain.funding.Call;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@JBossLog
public class EmailService implements NotificationService {

  public EmailService(Mailer mailer, EmailFactory emailFactory) {
    this.mailer = mailer;
    this.emailFactory = emailFactory;
  }

  private final Mailer mailer;
  private final EmailFactory emailFactory;

  private final ConcurrentLinkedQueue<Mail> pendingEmails = new ConcurrentLinkedQueue<>();
  @ConfigProperty(name = "fundify.notification.email.batch-size")
  Integer batchSize;

  @Override
  public void addNotificationToQueue(Call call) {
    log.info("Adding notification for call to queue: " + call.getId());

    List<Mail> mails = Optional.ofNullable(call.getSubscriptions())
        .stream()
        .flatMap(List::stream)
        .map(sub -> emailFactory.createNotificationEmail(sub, call))
        .toList();

    pendingEmails.addAll(mails);
  }

  @Scheduled(every = "{fundify.notification.every.interval.email-poll}")
  public void processPendingEmails() {

    for (int i = 0; i < batchSize; i++) {
      Mail mail = pendingEmails.poll();
      if (mail == null) {
        return;
      }
      mailer.send(mail);
    }
  }
}
