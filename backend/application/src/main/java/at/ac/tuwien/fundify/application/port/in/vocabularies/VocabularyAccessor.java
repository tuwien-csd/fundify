package at.ac.tuwien.fundify.application.port.in.vocabularies;

import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import java.util.List;

public interface VocabularyAccessor {

    Vocabulary getById(VocabularyId id) throws EntityNotFoundException;

    List<Vocabulary> getByUniversityId(UniversityId universityId);

    List<Vocabulary> getAll();

}