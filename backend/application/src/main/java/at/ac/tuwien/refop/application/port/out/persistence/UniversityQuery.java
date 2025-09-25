package at.ac.tuwien.refop.application.port.out.persistence;

import at.ac.tuwien.refop.domain.annotating.University;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.annotating.UniversityReference;
import at.ac.tuwien.refop.domain.common.RisId;
import at.ac.tuwien.refop.domain.dto.UniversityIdMapping;

import java.util.List;
import java.util.Optional;

public interface UniversityQuery {

    Optional<UniversityIdMapping> findByRisId(RisId risId);

    Optional<University> findById(UniversityId id);

    List<University> list();

    Optional<UniversityReference> findReferenceById(UniversityId id);

    Optional<UniversityReference> findReferenceByEmailDomain(String emailDomain);
}
