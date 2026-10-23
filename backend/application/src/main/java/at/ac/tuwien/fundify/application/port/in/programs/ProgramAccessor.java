package at.ac.tuwien.fundify.application.port.in.programs;

import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.ProgramVersion;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import java.util.List;


public interface ProgramAccessor {

    Program getById(ProgramId id) throws EntityNotFoundException;

    List<Program> getAll();

    Program getProgramByRisIdAndStatus(RisId risId, EPublicationStatus status)
        throws EntityNotFoundException;

  List<Program> listPublishedProgramsFilteredBy(
      ETargetGroup targetGroup,
      EAustrianState state,
      FunderId funderId,
      ERegionalScope scope
  );

    List<ProgramVersion> getVersionsByProgramId(ProgramId programId);
}