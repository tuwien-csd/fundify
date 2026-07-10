package at.ac.tuwien.fundify.application.service.programs;

import at.ac.tuwien.fundify.application.port.in.programs.ProgramAccessor;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramQuery;
import at.ac.tuwien.fundify.application.port.out.persistence.ProgramVersionRepository;
import at.ac.tuwien.fundify.application.service.common.BasePermissionService;
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
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProgramAccessorImpl implements ProgramAccessor {

    private final ProgramQuery programQuery;
    private final ProgramVersionRepository programVersionRepository;
    private final BasePermissionService basePermissionService;

    @Override
    public Program getById(ProgramId id) throws EntityNotFoundException {
        return programQuery.find(id).orElseThrow(() -> EntityNotFoundException.programNotFound(
            id.value()));
    }

    @Override
    public List<Program> getAll() {
      return programQuery.findAll()
          .stream()
          .filter(currentUserCanRead())
          .toList();
    }

    @Override
    public Program getProgramByRisIdAndStatus(RisId risId, EPublicationStatus status)
        throws EntityNotFoundException {
      final var program = programQuery.find(risId, EPublicationStatus.PUBLISHED);
      return program.orElseThrow(() -> EntityNotFoundException.programNotFound(
          risId.id()));
    }

    @Override
    public List<Program> listPublishedProgramsFilteredBy(ETargetGroup targetGroup, EAustrianState state,
        FunderId funderId, ERegionalScope scope) {
      return programQuery.find(targetGroup, state, funderId, scope, EPublicationStatus.PUBLISHED);
    }

    @Override
    public List<ProgramVersion> getVersionsByProgramId(ProgramId programId) {
        return programVersionRepository.findByProgramId(programId);
    }

    /**
     * Predicate to check if the current user can read the program. Important note: If you use
     * this on optionals to filter, you might send a NOT_FOUND exception, when the user really just
     * has no permission to read the item. Depending on your logic, this can be fine.
     */
    private Predicate<Program> currentUserCanRead() {
      return item -> item.getStatus() == EPublicationStatus.PUBLISHED
          || basePermissionService.currentUserIsAffiliatedWith(item.getFunder())
          || basePermissionService.isUserAdmin();
    }
}
