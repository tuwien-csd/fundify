package at.ac.tuwien.refop.application.service.programs;

import at.ac.tuwien.refop.application.port.in.programs.ProgramAccessor;
import at.ac.tuwien.refop.application.port.out.persistence.ProgramQuery;
import at.ac.tuwien.refop.application.service.common.BasePermissionService;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.ProgramId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.funding.Program;
import at.ac.tuwien.refop.domain.funding.ProgramReference;
import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProgramAccessorImpl implements ProgramAccessor {

    private final ProgramQuery programQuery;
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
    public List<Program> getByStatus(EPublicationStatus status) {
        return programQuery.find(status);
    }

    @Override
    public List<ProgramReference> getPublishedReferences() {
        return programQuery.findReference(EPublicationStatus.PUBLISHED);
    }

    @Override
    public List<Program> getByFunderAndStatus(FunderId funderId, EPublicationStatus status) {
        return programQuery.find(funderId, status);
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
    public ProgramReference getReference(ProgramId programId) throws EntityNotFoundException {
        return programQuery.findReference(programId).orElseThrow(() -> EntityNotFoundException.programNotFound(
            programId.value()));
    }

    @Override
    public List<ProgramReference> getReferenceByFunder(FunderId funderId) {
        return programQuery.findReference(funderId);
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
