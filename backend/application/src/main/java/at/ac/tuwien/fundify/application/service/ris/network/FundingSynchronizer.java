package at.ac.tuwien.fundify.application.service.ris.network;

import at.ac.tuwien.fundify.application.port.common.FunderNotFoundException;
import at.ac.tuwien.fundify.application.port.common.ProgramNotFoundException;
import at.ac.tuwien.fundify.application.port.in.ris.network.SyncExternalFundingsUseCase;
import at.ac.tuwien.fundify.application.port.out.persistence.CallRepository;
import at.ac.tuwien.fundify.application.port.out.persistence.FunderRepository;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramQuery;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramRepository;
import at.ac.tuwien.fundify.application.port.out.ris.network.FundingRemoteRepository;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.ProgramReference;
import at.ac.tuwien.fundify.domain.funding.vo.Identifier;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EEntryOrigin;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EIdentifierType;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;


@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
public class FundingSynchronizer implements SyncExternalFundingsUseCase {

  private final FundingRemoteRepository fundingRemoteRepository;
  private final CallRepository callRepository;
  private final ProgramRepository programRepository;
  private final ProgramQuery programQuery;
  private final FunderRepository funderRepository;

  private static final EEntryOrigin origin = EEntryOrigin.ENDPOINT;
  private static final EPublicationStatus status = EPublicationStatus.PUBLISHED;

  @Override
  public void synchronizeExternalPrograms() {
    List<Program> programs = fundingRemoteRepository.fetchAllPrograms();
    LocalDateTime now = LocalDateTime.now();
    programs.forEach(program -> {
      try {
        processProgram(program, now);
      } catch (FunderNotFoundException e) {
        log.info("No funder found with RIS ID:" + program.getFunder().risId());
      }
    });
  }

  @Override
  public void synchronizeExternalCalls() {
    List<Call> calls = fundingRemoteRepository.fetchAllCalls();
    LocalDateTime now = LocalDateTime.now();
    calls.forEach(call -> {
      try {
        processCall(call, now);
      } catch (ProgramNotFoundException e) {
        log.info("No program found with RIS ID: " + call.getPartOf().risId());
      } catch (FunderNotFoundException e) {
        log.info("No funder found with RIS ID:" + call.getFunder().risId());
      }
    });
  }

  private void processProgram(Program program, LocalDateTime now) {
    assignEntryOriginAndStatusOfExternalProgram(program);

    program.setFunder(getProgramFunder(program));
    program.setLastSync(now);

    Optional<Program> storedProgram = programQuery.find(program.getExternalIdentifier().getRisId());
    storedProgram.ifPresentOrElse(
        existing -> updateExistingProgram(program, existing),
        () -> persistNewProgram(program, now)
    );
  }

  private void updateExistingProgram(Program program, Program existing) {
    program.setRegistrationDate(existing.getRegistrationDate());
    program.setId(existing.getId());
    programRepository.update(program);
  }

  private void persistNewProgram(Program program, LocalDateTime now) {
    program.setRegistrationDate(now);
    programRepository.persist(program);
  }

  private void processCall(Call call, LocalDateTime now) {
    assignEntryOriginAndStatusOfExternalCall(call);

    call.setPartOf(getPartOfIfReferenceExists(call));
    call.setFunder(getCallMainFunder(call));
    call.setJointCallPartner(getJointCallpartnerIfReferencesExist(call));
    call.setLastSync(now);

    Optional<Call> storedCall = findExistingCall(call);
    storedCall.ifPresentOrElse(
        existing -> updateExistingCall(call, existing),
        () -> persistNewCall(call, now)
    );
  }

  private Optional<Call> findExistingCall(Call call) {
    Identifier euId = call.getExternalIdentifier().getIdentifier(EIdentifierType.EU_ID);
    if (euId != null) {
      return callRepository.findByEuId(euId.value());
    }
    return callRepository.findByRisId(call.getExternalIdentifier().getRisId());
  }

  private void updateExistingCall(Call call, Call existing) {
    call.setRegistrationDate(existing.getRegistrationDate());
    call.setId(existing.getId());
    callRepository.update(call);
  }

  private void persistNewCall(Call call, LocalDateTime now) {
    call.setRegistrationDate(now);
    callRepository.persist(call);
  }

  private void assignEntryOriginAndStatusOfExternalCall(Call call) {
    call.setEntryOrigin(origin);
    call.setStatus(status);
  }

  private void assignEntryOriginAndStatusOfExternalProgram(Program program) {
    program.setEntryOrigin(origin);
    program.setStatus(status);
  }

  private FunderReference getProgramFunder(Program program) {
    return funderRepository.findReferenceByRisId(program.getFunder().risId())
        .orElseThrow(FunderNotFoundException::new);
  }

  private FunderReference getCallMainFunder(Call call) {
    return funderRepository.findReferenceByRisId(call.getFunder().risId())
        .orElseThrow(FunderNotFoundException::new);
  }

  private List<FunderReference> getJointCallpartnerIfReferencesExist(Call call) {
    // There are potential funders without RIS ID, so we need to check if we have it in db
    // - if we have it in db we save only the reference id
    // - if we do not have it in db we save full funder reference without RIS ID
    //   In this case, no query by reference id is possible
    if (call.getJointCallPartner() == null) {
      return List.of();
    }
    return call
        .getJointCallPartner()
        .stream()
        .filter(it -> {
          boolean risIdIsSet = Objects.nonNull(it.risId());
          if (!risIdIsSet) {
            log.warn(String.format("Funder '%s' has no RIS ID and will be ignored.", it.name()));
          }
          return risIdIsSet;
        }) //Only lookup references with RIS ID. Otherwise, leave them out.
        .map(funder -> funderRepository.findReferenceByRisId(funder.risId()).orElse(funder))
        .toList();
  }

  private ProgramReference getPartOfIfReferenceExists(Call call) {
    if (call.getPartOf() == null) {
      return null;
    }
    return programQuery.findReference(call.getPartOf().risId())
        .orElseThrow(ProgramNotFoundException::new);
  }
}
