package at.ac.tuwien.fundify.application.port.in.institutions;

import at.ac.tuwien.fundify.domain.annotating.University;
import at.ac.tuwien.fundify.domain.annotating.UniversityCreate;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import java.util.List;

public interface UniversityUseCase {

  University addUniversity(UniversityCreate university);

  University getUniversity(UniversityId id) throws FundifyException;

  University getUniversityByAcronym(String acronym) throws FundifyException;

  University updateUniversity(University university) throws FundifyException;

  void deleteUniversity(UniversityId id) throws FundifyException;
  List<University> getAllUniversity();

}
