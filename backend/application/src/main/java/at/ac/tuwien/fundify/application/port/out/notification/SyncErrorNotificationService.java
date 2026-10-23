package at.ac.tuwien.fundify.application.port.out.notification;

import java.util.List;

public interface SyncErrorNotificationService {

  void sendSyncErrorNotification(String memberId, String contactEmail, List<String> errorDetails);

}
