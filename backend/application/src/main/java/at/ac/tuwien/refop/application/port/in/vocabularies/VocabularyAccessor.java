package at.ac.tuwien.refop.application.port.in.vocabularies;

import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.Vocabulary;
import at.ac.tuwien.refop.domain.common.VocabularyId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import java.util.List;

public interface VocabularyAccessor {

    Vocabulary getById(VocabularyId id) throws EntityNotFoundException;

    List<Vocabulary> getByUniversityId(UniversityId universityId);

    List<Vocabulary> getAll();

}