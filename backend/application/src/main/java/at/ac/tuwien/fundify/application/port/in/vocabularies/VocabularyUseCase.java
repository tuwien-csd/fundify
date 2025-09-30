package at.ac.tuwien.fundify.application.port.in.vocabularies;

import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import java.util.List;

public interface VocabularyUseCase {

    VocabularyId addEntry(VocabularyId id, String entry) throws FundifyException;

    VocabularyId deleteEntry(VocabularyId id, String entry) throws FundifyException;

  Vocabulary getById(VocabularyId id) throws EntityNotFoundException;

  List<Vocabulary> getAll();
}