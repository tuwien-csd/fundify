package at.ac.tuwien.fundify.application.port.in.programs;

import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.ProgramReference;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import java.util.List;


public interface ProgramAccessor {

  //TODO: Introduce filter object to get rid of all these methods

    Program getById(ProgramId id) throws EntityNotFoundException;

    List<Program> getAll();

    List<Program> getByStatus(EPublicationStatus status);

    List<ProgramReference> getPublishedReferences();

    List<Program> getByFunderAndStatus(FunderId funderId, EPublicationStatus status);

    Program getProgramByRisIdAndStatus(RisId risId, EPublicationStatus status)
        throws EntityNotFoundException;

  List<Program> listPublishedProgramsFilteredBy(
      ETargetGroup targetGroup,
      EAustrianState state,
      FunderId funderId,
      ERegionalScope scope
  );

    ProgramReference getReference(ProgramId programId) throws EntityNotFoundException;

    List<ProgramReference> getReferenceByFunder(FunderId funderId);
}