package at.ac.tuwien.refop.application.port.in.vocabularies;

import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.common.VocabularyId;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import java.util.List;

public interface VocabularyUseCase {

    VocabularyId addEntry(VocabularyId id, String entry) throws FundifyException;

    VocabularyId deleteEntry(VocabularyId id, String entry) throws FundifyException;

    List<VocabularyId> ensureInitialized(UniversityId universityId) throws FundifyException;
}