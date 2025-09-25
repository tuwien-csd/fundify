package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.annotating.University;
import at.ac.tuwien.fundify.domain.annotating.UniversityCreate;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import java.util.List;
import java.util.Optional;

public interface UniversityRepository {

    University persist(University university);

    University add(UniversityCreate universityCreate);

    Optional<University> update(University university);

    Optional<University> findById(UniversityId id);

    List<University> listAllUniversities();

    boolean delete(UniversityId id);
}
