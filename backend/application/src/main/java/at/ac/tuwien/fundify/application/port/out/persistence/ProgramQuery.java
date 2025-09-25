package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.funding.Program;
import at.ac.tuwien.fundify.domain.funding.ProgramReference;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ERegionalScope;
import java.util.List;
import java.util.Optional;

public interface ProgramQuery {

    Optional<Program> find(ProgramId programId);

    Optional<Program> find(RisId risId);

    Optional<Program> find(RisId risId, EPublicationStatus status);

    List<Program> findAll();
    List<Program> find(
            ETargetGroup targetGroup,
            EAustrianState region,
            FunderId funderId,
            ERegionalScope applicantsScope,
            EPublicationStatus status);

    List<Program> find(EPublicationStatus status);

    List<Program> find(FunderId funderId, EPublicationStatus status);

    Optional<ProgramReference> findReference(ProgramId programId);

    Optional<ProgramReference> findReference(RisId risId);

    List<ProgramReference> findReference(EPublicationStatus status);

    List<ProgramReference> findReference(FunderId funderId);
}
