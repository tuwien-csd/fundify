package at.ac.tuwien.refop.application.port.out.persistence;

import at.ac.tuwien.refop.domain.common.Vocabulary;
import at.ac.tuwien.refop.domain.common.VocabularyId;
import java.util.Optional;

public interface VocabularyRepository {

    VocabularyId persistVocabulary(Vocabulary vocabulary);

    VocabularyId updateVocabulary(Vocabulary vocabulary);

    Optional<Vocabulary> getById(VocabularyId id);
}
