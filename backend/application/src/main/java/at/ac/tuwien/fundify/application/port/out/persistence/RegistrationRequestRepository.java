package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import java.util.List;

public interface RegistrationRequestRepository {
  RegistrationRequest save(RegistrationRequest request);

  List<RegistrationRequest> findAllRequests();

  void deleteById(String id);
}
