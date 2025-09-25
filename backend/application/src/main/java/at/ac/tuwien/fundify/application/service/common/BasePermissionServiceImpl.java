package at.ac.tuwien.fundify.application.service.common;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import at.ac.tuwien.fundify.domain.funding.Funder;
import at.ac.tuwien.fundify.domain.funding.FunderReference;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class BasePermissionServiceImpl implements BasePermissionService {

  private final UserService userService;


  @Override
  public boolean currentUserIsAffiliatedWith(Funder funder) {
    if (funder == null) return false;
    return currentUserIsAffiliatedWith(funder.getAcronym());
  }

  @Override
  public boolean currentUserIsAffiliatedWith(FunderReference funder) {
    if (funder == null) return false;
    return currentUserIsAffiliatedWith(funder.acronym());
  }

  @Override
  public boolean currentUserIsAffiliatedWith(UniversityReference uniRef) {
   if(uniRef == null) return false;
   return currentUserIsAffiliatedWith(uniRef.acronym());
  }

  @Override
  public boolean currentUserIsAffiliatedWith(String institutionAcronym) {
    if (institutionAcronym == null) return false;
    String currentUserAffiliationId = userService.getCurrentUserAffiliationId();
    if (currentUserAffiliationId == null) {
      return false;
    }
    boolean isCurrentUserAffiliatedWithFunder = currentUserAffiliationId.equalsIgnoreCase(
        institutionAcronym);

    if (!isCurrentUserAffiliatedWithFunder) {
      log.infof(
          "User '%s' is affiliated with %s and therefore not has no write permission for entities which are affiliated with %s.",
          userService.getCurrentUserIdAndName(), userService.getCurrentUserAffiliationId(),
          institutionAcronym);
    }
    return isCurrentUserAffiliatedWithFunder;
  }

  @Override
  public boolean isUserAdmin() {
    return userService.isUserAdmin();
  }

  @Override
  public boolean isUserAnnotator() {
      return userService.isUserAnnotator();
  }
}
