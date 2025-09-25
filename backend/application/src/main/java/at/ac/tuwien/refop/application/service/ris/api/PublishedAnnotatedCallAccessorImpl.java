package at.ac.tuwien.refop.application.service.ris.api;

import at.ac.tuwien.refop.application.port.in.ris.api.PublishedAnnotatedCallAccessor;
import at.ac.tuwien.refop.application.port.out.persistence.AnnotatedCallQuery;
import at.ac.tuwien.refop.application.port.out.persistence.CallRepository;
import at.ac.tuwien.refop.application.port.out.persistence.UniversityQuery;
import at.ac.tuwien.refop.application.service.common.BasePermissionService;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.ETargetGroup;
import at.ac.tuwien.refop.domain.common.FunderId;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.dto.AnnotatedCallDTO;
import at.ac.tuwien.refop.domain.dto.CallIdMapping;
import at.ac.tuwien.refop.domain.dto.UniversityIdMapping;
import at.ac.tuwien.refop.domain.funding.vo.enums.EAustrianState;
import at.ac.tuwien.refop.domain.funding.vo.enums.ECallType;
import at.ac.tuwien.refop.domain.funding.vo.enums.ERegionalScope;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class PublishedAnnotatedCallAccessorImpl implements PublishedAnnotatedCallAccessor {

  private final AnnotatedCallQuery annotatedCallQuery;
  private final UniversityQuery universityQuery;
  private final CallRepository callRepository;
  private final BasePermissionService basePermissionService;

  @Override
  public AnnotatedCallDTO getPublishedByUniversityRisIdAndCallRisId(RisId universityRisId,
      RisId callRisId)
      throws EntityNotFoundException {
    UniversityIdMapping university = universityQuery.findByRisId(universityRisId)
        .orElseThrow(() -> EntityNotFoundException.universityNotFound(universityRisId.toString()));
    CallIdMapping callIdMapping = callRepository.findIdMappingByRisId(callRisId)
        .orElseThrow(() -> EntityNotFoundException.callNotFound(callRisId.toString()));
    return annotatedCallQuery
        .find(university.internalId(), callIdMapping.internalId(), EPublicationStatus.PUBLISHED)
        .filter(currentUserCanRead())
        .orElseThrow(
            () -> EntityNotFoundException.annotatedCallNotFound(university.internalId().toString(),
                callIdMapping.internalId().toString()));
  }

  @Override
  public List<AnnotatedCallDTO> listByUniversityAndCallFilter(RisId universityRisId,
      ECallType callType, ETargetGroup targetGroup, Boolean runningCalls, EAustrianState region,
      FunderId funderId, ERegionalScope applicantsScope)
      throws EntityNotFoundException {
    UniversityIdMapping universityIdMapping = universityQuery.findByRisId(universityRisId)
        .orElseThrow(() -> EntityNotFoundException.universityNotFound(universityRisId.toString()));
    return annotatedCallQuery
        .find(universityIdMapping.internalId(), callType, targetGroup,
            runningCalls, region, funderId, applicantsScope,
            EPublicationStatus.PUBLISHED)
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
