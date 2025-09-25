package at.ac.tuwien.fundify.application.port.in.vocabularies;

import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import java.util.List;

public interface VocabularyUseCase {

    VocabularyId addEntry(VocabularyId id, String entry) throws FundifyException;

    VocabularyId deleteEntry(VocabularyId id, String entry) throws FundifyException;

    List<VocabularyId> ensureInitialized(UniversityId universityId) throws FundifyException;
}