package at.ac.tuwien.refop.application.port.in.institutions;

import at.ac.tuwien.refop.domain.annotating.University;
import at.ac.tuwien.refop.domain.annotating.UniversityCreate;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import java.util.List;

public interface UniversityUseCase {

  University addUniversity(UniversityCreate university);

  University getUniversity(UniversityId id) throws FundifyException;

  University getUniversityByAcronym(String acronym) throws FundifyException;

  University updateUniversity(University university) throws FundifyException;

  void deleteUniversity(UniversityId id) throws FundifyException;
  List<University> getAllUniversity();

}
