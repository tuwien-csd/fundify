package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import java.util.List;
import java.util.Optional;

public interface VocabularyQuery {

    Optional<Vocabulary> findById(VocabularyId id);

    List<Vocabulary> findByUniversityId(UniversityId universityId);

    List<Vocabulary> findAll();
}
