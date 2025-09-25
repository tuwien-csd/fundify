package at.ac.tuwien.fundify.application.service.common;

import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import at.ac.tuwien.fundify.domain.funding.Funder;
import at.ac.tuwien.fundify.domain.funding.FunderReference;

public interface BasePermissionService {

  boolean currentUserIsAffiliatedWith(Funder funder);
  boolean currentUserIsAffiliatedWith(FunderReference funder);
  boolean currentUserIsAffiliatedWith(UniversityReference uniRef);
  boolean currentUserIsAffiliatedWith(String institutionAcronym);
  boolean isUserAdmin();
  boolean isUserAnnotator();
}
