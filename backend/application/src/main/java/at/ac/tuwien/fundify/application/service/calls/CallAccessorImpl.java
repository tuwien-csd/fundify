package at.ac.tuwien.fundify.application.service.calls;

import at.ac.tuwien.fundify.application.port.in.calls.CallAccessor;
import at.ac.tuwien.fundify.application.port.out.persistence.CallQuery;
import at.ac.tuwien.fundify.application.port.out.persistence.CallVersionRepository;
import at.ac.tuwien.fundify.application.service.common.BasePermissionService;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.CallOwner;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.funding.Call;
import at.ac.tuwien.fundify.domain.funding.CallVersion;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CallAccessorImpl implements CallAccessor {

    private final CallQuery callQuery;
    private final CallVersionRepository callVersionRepository;
    private final BasePermissionService basePermissionService;

    @Override
    public Call getById(CallId id) throws EntityNotFoundException {
        return callQuery.find(id).orElseThrow(() -> EntityNotFoundException.callNotFound(id.toString()));
    }

    @Override
    public List<Call> getAll() {
      return callQuery.findAll()
          .stream()
          .filter(currentUserCanRead())
          .toList();
    }

  @Override
    public List<Call> getByStatus(EPublicationStatus status) {
        return callQuery.find(status);
    }

    @Override
    public List<CallVersion> getVersionsByCallId(CallId callId) {
        return callVersionRepository.findByCallId(callId);
    }

  /**
   * Predicate to check if the current user can read the call. Important note: If you use
   * this on optionals to filter, you might send a NOT_FOUND exception, when the user really just
   * has no permission to read the item. Depending on your logic, this can be fine.
   */
  private Predicate<Call> currentUserCanRead() {
      return item -> {
          if (basePermissionService.isUserAdmin()) {
              return true;
          }

          if (item.getStatus() == EPublicationStatus.PUBLISHED) return true;
          return Optional
                  .ofNullable(item.getCallOwner())
                  .map(CallOwner::getAcronym)
                  .map(basePermissionService::currentUserIsAffiliatedWith)
                  .orElse(false);
      };
  }
}