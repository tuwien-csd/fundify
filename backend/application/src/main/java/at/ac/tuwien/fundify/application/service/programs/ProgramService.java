package at.ac.tuwien.fundify.application.service.programs;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.programs.ProgramUseCase;
import at.ac.tuwien.fundify.application.port.out.persistence.FunderRepository;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramRepository;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EUpdateSource;
import at.ac.tuwien.fundify.application.service.common.BasePermissionService;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.common.exceptions.InsufficientPermissionsException;
import at.ac.tuwien.fundify.domain.common.exceptions.ProgramAlreadyPublishedException;
import at.ac.tuwien.fundify.domain.common.exceptions.RisIdMustNotBeSetException;
import at.ac.tuwien.fundify.domain.common.exceptions.UnexpectedErrorException;
import at.ac.tuwien.fundify.domain.funding.Funder;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EEntryOrigin;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class ProgramService implements ProgramUseCase {

  private final ProgramRepository programRepository;
  private final ProgramVersioningService programVersioningService;
  private final BasePermissionService basePermissionService;
  private final FunderRepository funderRepository;
  private final UserService userService;

  @Override
  public Program addProgram(Program program) throws FundifyException {
    log.infof("Creation of a program initiated by user %s", userService.getCurrentUserIdAndName());

    FunderId funderId = program.getFunder().id();
    Funder funder = funderRepository.findById(funderId)
        .orElseThrow(() -> EntityNotFoundException.funderNotFound(funderId.toString()));

    if (currentUserMayNotWrite(program, funder.getAcronym())) {
      throw new InsufficientPermissionsException();
    }

    if (program.getExternalIdentifier().getRisId() != null) {
      throw new RisIdMustNotBeSetException("Program");
    }
    program.setEntryOrigin(EEntryOrigin.REFOP);
    program = programRepository.persist(program);
    // consider separate method for publishing
    if (program.publicationStatus() == EPublicationStatus.PUBLISHED) {
      program.publish(funder.getAcronym());
      program = programRepository.update(program)
          .orElse(null);  // program data currently not validated
    }
    if (program != null) {
      log.infof("User %s successfully created program %s", userService.getCurrentUserIdAndName(),
          program.getId().value());
      return program;
    } else {
      return null;
    }
  }

  @Override
  public Program getProgram(ProgramId id) throws FundifyException {
    return programRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.programNotFound(id.value()));
  }

  @Override
  public Program updateProgram(Program program) throws FundifyException {
    log.infof("Update for program %s initiated by user %s", program.getId().value(),
        userService.getCurrentUserIdAndName());

    Program programFromDb = programRepository.findById(program.getId())
        .orElseThrow(() -> EntityNotFoundException.programNotFound(program.getId().value()));
    programVersioningService.createVersionIfChanged(programFromDb, program, EUpdateSource.MANUAL);
    FunderReference funder = program.getFunder();

    if (currentUserMayNotWrite(programFromDb, funder.acronym())) {
      throw new InsufficientPermissionsException(programFromDb.getId().value());
    }

    if (program.publicationStatus() == EPublicationStatus.PUBLISHED) {
      if (program.getExternalIdentifier().getRisId() == null) {
        program.publish(funder.acronym());
      } else {
        updateLastSyncDate(program);
      }
    }
    return programRepository.update(program).orElseThrow(
        () -> new UnexpectedErrorException("Error updating program with ID: " + programFromDb.getId().value()));
  }

  @Override
  public void deleteProgram(ProgramId id) throws FundifyException {
    log.infof("Deletion of program %s initiated by user %s", id.value(),
        userService.getCurrentUserIdAndName());
    final var program = programRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.programNotFound(id.value()));

    if (currentUserMayNotWrite(program, program.getFunder().acronym())) {
      throw new InsufficientPermissionsException(program.getId().value());
    }

    if (program.publicationStatus() == EPublicationStatus.PUBLISHED) {
      throw new ProgramAlreadyPublishedException(id.value());
    }

    boolean deleted = programRepository.delete(id);
    if (!deleted) {
      throw new UnexpectedErrorException("Error deleting program with ID: " + id);
    }
  }

  private void updateLastSyncDate(Program program) {
    LocalDateTime now = LocalDateTime.now();
    program.setLastSync(now);
  }


  private boolean currentUserMayNotWrite(Program program, String funderAcronym) {
    //If the program is managed through the sync, it cannot be updated via the API.
    if (EEntryOrigin.ENDPOINT.equals(program.getEntryOrigin())) {
      log.infof(
          "Program with id '%s' cannot be updated via the API because it is managed through the sync.",
          program.getId().value());
      return true;
    }
    //Admins can write all other programs.
    if (userService.isUserAdmin()) {
      return false;
    }
    //Else, funders can only write programs they are affiliated with.
    return !basePermissionService.currentUserIsAffiliatedWith(funderAcronym);
  }
}
