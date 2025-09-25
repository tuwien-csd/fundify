package at.ac.tuwien.refop.adapters.in.rest.auth;

import at.ac.tuwien.refop.application.port.common.UserService;
import at.ac.tuwien.refop.application.port.out.permissions.UserPermissionsProvider;
import at.ac.tuwien.refop.domain.common.FundifyUser;
import at.ac.tuwien.refop.domain.common.UserPermissionHolder;
import at.ac.tuwien.refop.domain.common.UserRole;
import at.ac.tuwien.refop.domain.common.exceptions.NoUserPermissionsConfiguredException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
@JBossLog
public class JwtUserService implements UserService {

  private final SecurityIdentity securityIdentity;
  private static final String EMAIL_CLAIM_NAME = "email";
  private static final String SUB_CLAIM_NAME = "sub";
  private static final String NAME_CLAIM_NAME = "name";
  private final UserPermissionsProvider permissionsProvider;

  public JwtUserService(
      SecurityIdentity securityIdentity,
      @Named("dbProvider") UserPermissionsProvider permissionsProvider) {
    this.securityIdentity = securityIdentity;
    this.permissionsProvider = permissionsProvider;
  }

  @Override
  public String getCurrentUserId() {
    return this.getClaimValue(SUB_CLAIM_NAME);
  }


  @Override
  public String getCurrentUserEmail() {
    return this.getClaimValue(EMAIL_CLAIM_NAME);
  }

  @Override
  public String getCurrentUserName() {
    return this.getClaimValue(NAME_CLAIM_NAME);
  }

  /**
   * Returns the username and id in a string.
   *
   * @return Returns a string in the format "name: '', id: '123456789'"
   */
  @Override
  public String getCurrentUserIdAndName() {
    return String.format("name: '%s', id: '%s'", this.getCurrentUserName(),
        this.getCurrentUserId());
  }

  @Override
  public String getCurrentUserAffiliationId() {
    return this.getCurrentUserPermissionHolder().affiliationId();
  }

  @Override
  public boolean isUserAdmin() {
    return this.securityIdentity.hasRole(UserRole.Names.ADMIN);
  }

  @Override
  public boolean isUserAnnotator() {
        return this.securityIdentity.hasRole(UserRole.Names.ANNOTATOR);
  }

  @Override
  public UserPermissionHolder getCurrentUserPermissionHolder() {


    return permissionsProvider.getUserPermissions(getCurrentUserId())
        .orElseThrow(() -> new NoUserPermissionsConfiguredException(getCurrentUserId()));
  }

  @Override
  public FundifyUser getCurrentUser() {
    String id = this.getCurrentUserId();
    String username = this.getCurrentUserName();
    String affiliationId = this.getCurrentUserAffiliationId();
    String email = this.getClaimValue(EMAIL_CLAIM_NAME);
    return new FundifyUser(id, username, affiliationId, email);
  }

  private String getClaimValue(String claim) {
    return this.securityIdentity.getPrincipal(JsonWebToken.class).getClaim(claim);
  }
}
