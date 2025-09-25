package at.ac.tuwien.fundify.application.port.common;

import at.ac.tuwien.fundify.domain.common.UserPermissionHolder;

import at.ac.tuwien.fundify.domain.common.FundifyUser;

public interface UserService {

  String getCurrentUserId();

  String getCurrentUserName();

  String getCurrentUserIdAndName();

  String getCurrentUserAffiliationId();

  String getCurrentUserEmail();

  UserPermissionHolder getCurrentUserPermissionHolder();

  boolean isUserAdmin();

  FundifyUser getCurrentUser();

  boolean isUserAnnotator();
}
