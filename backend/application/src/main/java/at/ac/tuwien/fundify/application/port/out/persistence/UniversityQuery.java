package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.annotating.University;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.dto.UniversityIdMapping;

import java.util.List;
import java.util.Optional;

public interface UniversityQuery {

    Optional<UniversityIdMapping> findByRisId(RisId risId);

    Optional<University> findById(UniversityId id);

    List<University> list();

    Optional<UniversityReference> findReferenceById(UniversityId id);

    Optional<UniversityReference> findReferenceByEmailDomain(String emailDomain);
}
