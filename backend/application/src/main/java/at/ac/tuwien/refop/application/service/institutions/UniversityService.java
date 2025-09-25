package at.ac.tuwien.refop.application.service.institutions;

import at.ac.tuwien.refop.application.port.common.UserService;
import at.ac.tuwien.refop.application.port.in.institutions.UniversityUseCase;
import at.ac.tuwien.refop.application.port.out.persistence.UniversityRepository;
import at.ac.tuwien.refop.domain.annotating.University;
import at.ac.tuwien.refop.domain.annotating.UniversityCreate;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class UniversityService implements UniversityUseCase {

  private final UniversityRepository universityRepository;
  private final UserService userService;

  @Override
  public University addUniversity(UniversityCreate university) {
    log.infof("Creation of a university initiated by user %s",
        userService.getCurrentUserIdAndName());
    University created = universityRepository.add(university);
    log.infof("User %s successfully created university %s", userService.getCurrentUserIdAndName(),
        created.getId().value());
    return created;
  }

  @Override
  public University getUniversity(UniversityId id) throws FundifyException {
    return universityRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.universityNotFound(id.toString()));
  }

  @Override
  public University getUniversityByAcronym(String acronym) throws FundifyException {
      return this.getAllUniversity().stream()
            .filter(u -> u.getAcronym() != null
                    && u.getAcronym().equalsIgnoreCase(acronym))
            .findFirst()
            .orElseThrow(() -> EntityNotFoundException.universityNotFound(String.valueOf(acronym)));
  }

  @Override
  public University updateUniversity(University university) throws FundifyException {
    log.infof("Update for university %s initiated by user %s", university.getId().value(),
        userService.getCurrentUserIdAndName());
    if (universityRepository.findById(university.getId()).isEmpty()) {
      throw EntityNotFoundException.funderNotFound(university.getId().toString());
    }
    return universityRepository.update(university)
        .orElseThrow(() -> EntityNotFoundException.funderNotFound(university.getId().toString()));
  }

  @Override
  public void deleteUniversity(UniversityId id) throws FundifyException {
    log.infof("Deletion of university %s initiated by user %s", id.value(),
        userService.getCurrentUserIdAndName());
    boolean deleted = universityRepository.delete(id);
    if (!deleted) {
      throw EntityNotFoundException.funderNotFound(id.toString());
    }
  }

  @Override
  public List<University> getAllUniversity() {
    return universityRepository.listAllUniversities();
  }
}
