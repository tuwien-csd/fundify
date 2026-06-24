package at.ac.tuwien.fundify.application.port.in.registration;

import at.ac.tuwien.fundify.domain.registration.RegistrationRequest;
import java.util.List;

public interface RegistrationRequestUseCase {

  /**
   * Persists a new pending registration request submitted via the contact form.
   */
  RegistrationRequest submit(String name, String email, String kindOfInstitution, String message);

  /**
   * Returns all pending registration requests. Every stored request is pending by
   * definition; handling one (approve or reject) deletes it.
   */
  List<RegistrationRequest> getAll();

  /**
   * Deletes a request. Used both for rejecting a request and for cleaning it up
   * after the corresponding user has been created.
   */
  void delete(String id);
}
