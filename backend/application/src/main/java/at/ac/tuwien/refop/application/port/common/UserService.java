package at.ac.tuwien.refop.application.port.common;

import at.ac.tuwien.refop.domain.common.UserPermissionHolder;

import at.ac.tuwien.refop.domain.common.FundifyUser;

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
