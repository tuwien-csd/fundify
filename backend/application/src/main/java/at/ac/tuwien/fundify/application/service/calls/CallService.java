package at.ac.tuwien.fundify.application.service.calls;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.calls.CallUseCase;
import at.ac.tuwien.fundify.application.port.out.notification.NotificationService;
import at.ac.tuwien.fundify.application.port.out.persistence.CallRepository;
import at.ac.tuwien.fundify.application.port.out.persistence.FunderRepository;
import at.ac.tuwien.fundify.application.service.common.BasePermissionService;
import at.ac.tuwien.fundify.application.service.institutions.UniversityService;
import at.ac.tuwien.fundify.domain.annotating.University;
import at.ac.tuwien.fundify.domain.common.CallId;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ESubscriptionStatus;
import at.ac.tuwien.fundify.domain.common.FunderId;
import at.ac.tuwien.fundify.domain.common.FundifyUser;
import at.ac.tuwien.fundify.domain.common.exceptions.CallAlreadyPublishedException;
import at.ac.tuwien.fundify.domain.common.exceptions.CallRisIdIsSetException;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.common.exceptions.InsufficientPermissionsException;
import at.ac.tuwien.fundify.domain.common.exceptions.UnexpectedErrorException;
import at.ac.tuwien.fundify.domain.funding.*;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EEntryOrigin;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class CallService implements CallUseCase {

  private final CallRepository callRepository;
  private final BasePermissionService basePermissionService;
  private final FunderRepository funderRepository;
  private final UserService userService;
  private final UniversityService universityService;
  private final NotificationService notificationService;


  @Override
  public CallId addCall(CallCreate callCreate) throws FundifyException {
    log.infof("Creation of a call initiated by user %s", userService.getCurrentUserIdAndName());

    FunderId funderId = callCreate.getFunder().id();
    Funder funder = funderRepository.findById(funderId)
        .orElseThrow(() -> EntityNotFoundException.funderNotFound(funderId.toString()));

    Call call = CallCreateMapper.INSTANCE.createCallFromCallCreate(callCreate, new Call());

    if (userService.isUserAnnotator()) {
        String userAffiliation = userService.getCurrentUserAffiliationId();
        University university = universityService.getUniversityByAcronym(userAffiliation);
        call.setCallOwner(university);
    } else {
        call.setCallOwner(funder);
    }

    if (!currentUserMayWrite(call, funderId)) {
      throw new InsufficientPermissionsException();
    }

    if (call.getExternalIdentifier().getRisId() != null) {
      throw new CallRisIdIsSetException();
    }

    call.setEntryOrigin(EEntryOrigin.REFOP);
    call.setLastUpdatedAt(LocalDateTime.now()); // add timestamp for last update
    call.setSubscriptions(new ArrayList<>());
    call = callRepository.persist(call);
    // consider separate usecase method for publishing
    if (call.publicationStatus() == EPublicationStatus.PUBLISHED) {
      call.publish(funder.getAcronym());
      call = callRepository.update(call).orElse(null);  // call data currently not validated
    }

    if (call != null) {
      CallId createdCallId = call.getId();
      log.infof("User %s successfully created call %s", userService.getCurrentUserIdAndName(),
          createdCallId.value());
      return createdCallId;
    } else {
      return null;
    }
  }

  @Override
  public Call getCall(CallId id) throws EntityNotFoundException {
    return callRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.callNotFound(id.toString()));
  }

  @Override
  public CallId updateCall(CallUpdate call) throws FundifyException {
    log.infof("Update for call %s initiated by user %s", call.getId().value(),
        userService.getCurrentUserIdAndName());

    Call callFromDb = callRepository.findById(call.getId())
        .orElseThrow(() -> EntityNotFoundException.callNotFound(call.getId().toString()));
    FunderReference funderReference = call.getFunder();

    if (!currentUserMayWrite(callFromDb, funderReference.id())) {
      throw new InsufficientPermissionsException(call.getId().value());
    }

    if (call.publicationStatus() == EPublicationStatus.PUBLISHED) {
      if (call.getExternalIdentifier().getRisId() == null) {
        FunderReference funder = funderRepository.findReferenceById(call.getFunder().id())
            .orElseThrow(
                () -> EntityNotFoundException.funderNotFound(call.getFunder().id().toString()));
        call.publish(funder.acronym());
      } else {
        updateLastSyncDate(call);
      }
    }

    call.setLastUpdatedAt(LocalDateTime.now()); // add timestamp for last update
    Call callUpdate = CallUpdateMapper.INSTANCE.updateCallFromCallUpdate(call, callFromDb);
    Call updatedCall = callRepository.update(callUpdate)
        .orElseThrow(() -> new UnexpectedErrorException("Error updating program with ID: " + callFromDb.getId().value()));
    notificationService.addNotificationToQueue(updatedCall);
    return updatedCall.getId();
  }

  @Override
  public void deleteCall(CallId id)
      throws FundifyException {
    log.infof("Deletion of call %s initiated by user %s", id.value(),
        userService.getCurrentUserIdAndName());

    Call call = callRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.callNotFound(id.toString()));

    if (!currentUserMayWrite(call, call.getFunder().id())) {
      throw new InsufficientPermissionsException(call.getId().value());
    }

    if (call.publicationStatus() == EPublicationStatus.PUBLISHED) {
      throw new CallAlreadyPublishedException(id.toString());
    }
    boolean deleted = callRepository.delete(id);
    if (!deleted) {
      throw new UnexpectedErrorException("Error deleting call with ID: " + id);
    }
  }

  @Override
  public Call setSubscriptionForCurrentUser(@NonNull CallId id, @NonNull ESubscriptionStatus status, @NonNull FundifyUser user) throws FundifyException {
    log.infof("Subscription change to call %s initiated by user %s", id.value(),
        userService.getCurrentUserIdAndName());

    Call call = callRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.callNotFound(id.toString()));

    List<FundifyUser> subscriptions = call.getSubscriptions();
    if (subscriptions == null) {
      subscriptions = new ArrayList<>();
      call.setSubscriptions(subscriptions);
    }
    if (status.equals(ESubscriptionStatus.SUBSCRIBED)) {
      if (call.getSubscriptions().stream()
          .noneMatch(s -> s.id().equals(user.id()))) {
        call.getSubscriptions().add(new FundifyUser(user.id(), user.name(), null,
            user.email()));
      }
    } else {
      call.getSubscriptions().removeIf(s -> s.id().equals(user.id()));
    }

    return callRepository.update(call)
        .orElseThrow(() -> EntityNotFoundException.callNotFound(call.getId().toString()));
  }

  private void updateLastSyncDate(CallUpdate call) {
    LocalDateTime now = LocalDateTime.now();
    call.setLastSync(now);
  }

  private boolean currentUserMayWrite(Call call, FunderId funderId) {
    //If the call is managed through the sync, it cannot be updated via the API.
    if (EEntryOrigin.ENDPOINT.equals(call.getEntryOrigin())) {
      log.infof(
          "Call with id '%s' cannot be updated via the API because it is managed through the sync.",
          call.getId().value());
      return false;
    }
    // Admins can write all other calls.
    if (userService.isUserAdmin()) {
      return true;
    }

    String callOwnerAcronym = call.getCallOwner().getAcronym();
    // Annotators can only write calls for external funders. They can only write calls they created.
    if (userService.isUserAnnotator()) {
        Funder funder = funderRepository.findById(funderId)
                .orElseThrow(() -> EntityNotFoundException.funderNotFound(funderId.toString()));
        return funder.getExternallyAdministered() &&
                basePermissionService.currentUserIsAffiliatedWith(callOwnerAcronym);
    }
    // Funders can only write calls they are affiliated with.
    return basePermissionService.currentUserIsAffiliatedWith(callOwnerAcronym);
  }

  @Mapper
  interface CallUpdateMapper {

    CallUpdateMapper INSTANCE = Mappers.getMapper(CallUpdateMapper.class);

    @Mapping(target = "callOwner", ignore = true)
    Call updateCallFromCallUpdate(CallUpdate callUpdate, @MappingTarget Call call);
  }

  @Mapper
  interface CallCreateMapper {
      CallCreateMapper INSTANCE = Mappers.getMapper(CallCreateMapper.class);
      @Mapping(target = "callOwner", ignore = true)
      Call createCallFromCallCreate(CallCreate callCreate, @MappingTarget Call call);
  }
}
