package at.ac.tuwien.fundify.application.service.institutions;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.institutions.FunderUseCase;
import at.ac.tuwien.fundify.application.port.out.persistence.FunderRepository;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.funding.Funder;
import at.ac.tuwien.fundify.domain.funding.FunderCreate;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class FunderService implements FunderUseCase {

  private final FunderRepository funderRepository;
  private final UserService userService;

  @Override
  public Funder addFunder(FunderCreate funder) {
    log.infof("Creation of a funder initiated by user %s", userService.getCurrentUserIdAndName());
    Funder created = funderRepository.add(funder);
    log.infof("User %s successfully created funder %s", userService.getCurrentUserIdAndName(),
        created.getId().value());
    return created;
  }

  @Override
  public Funder getFunder(FunderId id) throws FundifyException {
    return funderRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.funderNotFound(id.toString()));
  }

  @Override
  public Funder updateFunder(Funder funder) throws FundifyException {
    log.infof("Update for funder %s initiated by user %s", funder.getId().value(),
        userService.getCurrentUserIdAndName());
    if (funderRepository.findById(funder.getId()).isEmpty()) {
      throw EntityNotFoundException.funderNotFound(funder.getId().toString());
    }
    return funderRepository.update(funder)
        .orElseThrow(() -> EntityNotFoundException.funderNotFound(funder.getId().toString()));
  }

  @Override
  public void deleteFunder(FunderId id) throws FundifyException {
    log.infof("Deletion of funder %s initiated by user %s", id.value(),
        userService.getCurrentUserIdAndName());
    boolean deleted = funderRepository.delete(id);
    if (!deleted) {
      throw EntityNotFoundException.funderNotFound(id.toString());
    }
  }

  @Override
  public List<Funder> getAllFunders() {
    return funderRepository.listAllFunders();
  }

  @Override
  public FunderReference getFunderReference(FunderId id) throws FundifyException {
    return funderRepository.findReferenceById(id)
        .orElseThrow(() -> EntityNotFoundException.funderNotFound(id.toString()));
  }

  @Override
  public List<FunderReference> getAllFunderReferences() {
    return funderRepository.listAllFunderReferences();
  }

  @Override
  public List<FunderReference> searchFunderReferences(String query) {
    return funderRepository.searchReferences(query);
  }
}