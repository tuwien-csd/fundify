package at.ac.tuwien.fundify.application.service.registration;

import at.ac.tuwien.fundify.application.port.in.registration.RegistrationRequestUseCase;
import at.ac.tuwien.fundify.application.port.out.persistence.RegistrationRequestRepository;
import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
public class RegistrationRequestService implements RegistrationRequestUseCase {

  private final RegistrationRequestRepository registrationRequestRepository;

  @Override
  public RegistrationRequest submit(
      String name, String email, String kindOfInstitution, String message) {
    log.infof("Capturing registration request for email %s", email);
    var request =
        new RegistrationRequest(
            UUID.randomUUID().toString(),
            name,
            email,
            kindOfInstitution,
            message,
            Instant.now());
    return registrationRequestRepository.save(request);
  }

  @Override
  public List<RegistrationRequest> getAll() {
    return registrationRequestRepository.findAllRequests();
  }

  @Override
  public void delete(String id) {
    log.infof("Deleting registration request %s", id);
    registrationRequestRepository.deleteById(id);
  }
}
