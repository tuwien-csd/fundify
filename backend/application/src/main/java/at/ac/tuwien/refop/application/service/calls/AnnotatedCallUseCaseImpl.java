package at.ac.tuwien.refop.application.service.calls;

import at.ac.tuwien.refop.application.port.common.UserService;
import at.ac.tuwien.refop.application.port.in.calls.AnnotatedCallUseCase;
import at.ac.tuwien.refop.application.port.out.persistence.AnnotatedCallQuery;
import at.ac.tuwien.refop.application.port.out.persistence.AnnotatedCallRepository;
import at.ac.tuwien.refop.application.port.out.persistence.UniversityQuery;
import at.ac.tuwien.refop.application.service.common.BasePermissionService;
import at.ac.tuwien.refop.domain.annotating.AnnotatedCall;
import at.ac.tuwien.refop.domain.annotating.AnnotatedCallId;
import at.ac.tuwien.refop.domain.annotating.CallAnnotation;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.annotating.UniversityReference;
import at.ac.tuwien.refop.domain.common.CallId;
import at.ac.tuwien.refop.domain.common.ESubscriptionStatus;
import at.ac.tuwien.refop.domain.common.FundifyUser;
import at.ac.tuwien.refop.domain.common.exceptions.AnnotatedCallAlreadyAnnotatedException;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import at.ac.tuwien.refop.domain.common.exceptions.InsufficientPermissionsException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import java.time.LocalDateTime;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class AnnotatedCallUseCaseImpl implements AnnotatedCallUseCase {

  private final AnnotatedCallRepository annotatedCallRepository;
  private final AnnotatedCallQuery annotatedCallQuery;
  private final UniversityQuery universityQuery;
  private final BasePermissionService basePermissionService;
  private final UserService userService;
  private final CallService callService;

  @Override
  public AnnotatedCallId addAnnotation(CallId callId, UniversityId universityId,
      CallAnnotation annotation) throws FundifyException {
    log.infof("Creation of an annotation for call %s and university %s initiated by user %s",
        callId.value(), universityId.value(), userService.getCurrentUserIdAndName());
    if (annotatedCallQuery.find(callId, universityId).isPresent()) {
      throw new AnnotatedCallAlreadyAnnotatedException();
    }

    UniversityReference universityReference = universityQuery.findReferenceById(universityId)
        .orElseThrow(() -> EntityNotFoundException.universityNotFound(universityId.value()));

    if(!currentUserMayWrite(universityReference)) {
      throw new InsufficientPermissionsException(callId.value());
    }

    FundifyUser fundifyUser = userService.getCurrentUser();
    LocalDateTime lastUpdatedAt = LocalDateTime.now();
    AnnotatedCall annotatedCall = AnnotatedCall.createNew(callId, universityReference, annotation, lastUpdatedAt, fundifyUser);
    annotatedCall = annotatedCallRepository.persist(annotatedCall);
    log.infof("User %s successfully created annotation %s",
        userService.getCurrentUserIdAndName(), annotatedCall.getId().value());
    this.callService.setSubscriptionForCurrentUser(callId, ESubscriptionStatus.SUBSCRIBED, fundifyUser);
    return annotatedCall.getId();
  }

  @Override
  public AnnotatedCallId updateAnnotation(AnnotatedCallId id, CallAnnotation annotation)
      throws FundifyException {
    log.infof("Update for annotation %s initiated by user %s",
        id.value(), userService.getCurrentUserIdAndName());
    AnnotatedCall annotatedCall = annotatedCallRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.annotatedCallNotFound(id.value()));

    if(!currentUserMayWrite(annotatedCall.getUniversityReference())) {
      throw new InsufficientPermissionsException(id.value());
    }

    FundifyUser fundifyUser = userService.getCurrentUser();
    annotatedCall.setLastUpdatedAt(LocalDateTime.now());
    annotatedCall.setLastUpdatedBy(fundifyUser);
    annotatedCall.updateAnnotation(annotation);
    return annotatedCallRepository.update(annotatedCall).getId();
  }

  @Override
  public boolean deleteAnnotation(AnnotatedCallId id) throws FundifyException {
    log.infof("Deletion of annotation %s initiated by user %s",
        id.value(), userService.getCurrentUserIdAndName());
    var annotatedCall = annotatedCallRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.annotatedCallNotFound(id.value()));

    if(!currentUserMayWrite(annotatedCall.getUniversityReference())) {
      throw new InsufficientPermissionsException(id.value());
    }

    return annotatedCallRepository.delete(id);
  }

  @Override
  public AnnotatedCallId publish(AnnotatedCallId id) throws FundifyException {
    log.infof("Publishing of annotation %s initiated by user %s",
        id.value(), userService.getCurrentUserIdAndName());
    AnnotatedCall annotatedCall = annotatedCallRepository.findById(id)
        .orElseThrow(() -> EntityNotFoundException.annotatedCallNotFound(id.value()));
    if(!currentUserMayWrite(annotatedCall.getUniversityReference())) {
      throw new InsufficientPermissionsException(id.value());
    }
    annotatedCall.publish();
    return annotatedCallRepository.update(annotatedCall).getId();
  }

  private boolean currentUserMayWrite(UniversityReference uniRef) {
    return basePermissionService.currentUserIsAffiliatedWith(uniRef.acronym());
  }
}