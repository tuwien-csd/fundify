package at.ac.tuwien.fundify.application.port.out.notification;

import at.ac.tuwien.fundify.domain.funding.Call;

public interface NotificationService {

  void addNotificationToQueue(Call call);

}
