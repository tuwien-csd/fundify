package at.ac.tuwien.refop.application.port.out.persistence;

import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.Vocabulary;
import at.ac.tuwien.refop.domain.common.VocabularyId;
import java.util.List;
import java.util.Optional;

public interface VocabularyQuery {

    Optional<Vocabulary> findById(VocabularyId id);

    List<Vocabulary> findByUniversityId(UniversityId universityId);

    List<Vocabulary> findAll();
}
