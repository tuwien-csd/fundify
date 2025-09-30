package at.ac.tuwien.fundify.application.port.out.persistence;

import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import java.util.Optional;

public interface VocabularyRepository {

    Vocabulary persistVocabulary(Vocabulary vocabulary);

    VocabularyId updateVocabulary(Vocabulary vocabulary);

    Optional<Vocabulary> getById(VocabularyId id);
}
