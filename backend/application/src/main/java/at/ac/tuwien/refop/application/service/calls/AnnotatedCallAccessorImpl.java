package at.ac.tuwien.refop.application.service.calls;

import at.ac.tuwien.refop.application.port.in.calls.AnnotatedCallAccessor;
import at.ac.tuwien.refop.application.port.out.persistence.AnnotatedCallQuery;
import at.ac.tuwien.refop.application.service.common.BasePermissionService;
import at.ac.tuwien.refop.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.refop.domain.dto.CallPreviewDTO;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class AnnotatedCallAccessorImpl implements AnnotatedCallAccessor {

  private final AnnotatedCallQuery annotatedCallQuery;
  private final BasePermissionService basePermissionService;

  @Override
  public AnnotatedCallDTO getById(AnnotatedCallId id) throws EntityNotFoundException {
    return annotatedCallQuery
        .find(id)
        .filter(currentUserCanRead())
        .orElseThrow(() -> EntityNotFoundException.annotatedCallNotFound(id.toString()));
  }

  @Override
  public AnnotatedCallDTO getByCallIdAndUniversityId(CallId callId, UniversityId universityId)
      throws EntityNotFoundException {
    return annotatedCallQuery
        .find(callId, universityId)
        .filter(currentUserCanRead())
        .orElseThrow(() -> EntityNotFoundException.annotatedCallNotFound(universityId.toString(),
            callId.toString()));
  }

  @Override
  public CallPreviewDTO getCallPreviewByCallId(CallId callId)
      throws EntityNotFoundException {
    return annotatedCallQuery.find(callId).orElseThrow(() -> EntityNotFoundException.callNotFound(
        callId.value()));
  }

  @Override
  public List<AnnotatedCallDTO> getByUniversityId(UniversityId universityId) {
    return annotatedCallQuery
        .find(universityId)
        .stream()
        .filter(currentUserCanRead())
        .toList();
  }

  @Override
  public List<AnnotatedCallDTO> getAll() {
    return annotatedCallQuery.findAll()
        .stream()
        .filter(currentUserCanRead())
        .toList();
  }

  /**
   * Predicate to check if the current user can read the annotated call. Important note: If you use
   * this on optionals to filter, you might send a NOT_FOUND exception, when the user really just
   * has no permission to read the item. Depending on your logic, this can be fine.
   */
  private Predicate<AnnotatedCallDTO> currentUserCanRead() {
    return item -> basePermissionService.isUserAdmin()
        || basePermissionService.currentUserIsAffiliatedWith(item.universityReference());
  }
}