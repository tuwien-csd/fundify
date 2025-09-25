package at.ac.tuwien.refop.application.service.vocabularies;

import at.ac.tuwien.refop.application.port.common.UserService;
import at.ac.tuwien.refop.application.port.in.vocabularies.VocabularyUseCase;
import at.ac.tuwien.refop.application.port.out.persistence.UniversityQuery;
import at.ac.tuwien.refop.application.port.out.persistence.VocabularyQuery;
import at.ac.tuwien.refop.application.port.out.persistence.VocabularyRepository;
import at.ac.tuwien.refop.application.service.common.BasePermissionService;
import at.ac.tuwien.refop.domain.annotating.UniversityId;
import at.ac.tuwien.refop.domain.annotating.UniversityReference;
import at.ac.tuwien.refop.domain.common.EVocabularyType;
import at.ac.tuwien.refop.domain.common.Vocabulary;
import at.ac.tuwien.refop.domain.common.VocabularyId;
import at.ac.tuwien.refop.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.refop.domain.common.exceptions.FundifyException;
import at.ac.tuwien.refop.domain.common.exceptions.InsufficientPermissionsException;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;

@ApplicationScoped
@RequiredArgsConstructor
@JBossLog
public class VocabularyUseCaseImpl implements VocabularyUseCase {

  private final VocabularyRepository vocabularyRepository;
  private final VocabularyQuery vocabularyQuery;
  private final UniversityQuery universityQuery;
  private final UserService userService;
  private final BasePermissionService permissionService;

  @Override
  public VocabularyId addEntry(VocabularyId id, String entry) throws FundifyException {
    log.infof("Addition of entry '%s' to vocabulary %s initiated by user %s", entry, id.value(),
        userService.getCurrentUserIdAndName());
    Vocabulary vocabulary = vocabularyRepository.getById(id).orElseThrow(() ->
        EntityNotFoundException.vocabularyNotFound(id.value()));

    if(!currentUserMayWrite(vocabulary)) {
      throw new InsufficientPermissionsException(id.value());
    }

    vocabulary.entries().add(entry);
    return vocabularyRepository.updateVocabulary(vocabulary);

  }

  @Override
  public VocabularyId deleteEntry(VocabularyId id, String entry) throws FundifyException {
    log.infof("Deletion of entry '%s' from vocabulary %s initiated by user %s", entry, id.value(),
        userService.getCurrentUserIdAndName());
    Vocabulary vocabulary = vocabularyRepository.getById(id).orElseThrow(() ->
        EntityNotFoundException.vocabularyNotFound(id.value()));

    if(!currentUserMayWrite(vocabulary)) {
      throw new InsufficientPermissionsException(id.value());
    }

    vocabulary.entries().remove(entry);
    return vocabularyRepository.updateVocabulary(vocabulary);
  }

  @Override
  public List<VocabularyId> ensureInitialized(UniversityId universityId)
      throws EntityNotFoundException {
    // if the vocabulary list is empty, we initialize a vocabulary for each vocabulary type for the university
    List<VocabularyId> vocabularyIds = new ArrayList<>();
    if (vocabularyQuery.findByUniversityId(universityId).isEmpty()) {
      for (EVocabularyType type : EVocabularyType.values()) {
        vocabularyIds.add(initVocabulary(universityId, type));
      }
    }
    return vocabularyIds;
  }

  private VocabularyId initVocabulary(UniversityId universityId, EVocabularyType type)
      throws EntityNotFoundException {
    UniversityReference university = universityQuery.findReferenceById(universityId)
        .orElseThrow(() -> EntityNotFoundException.universityNotFound(universityId.value()));
    return vocabularyRepository.persistVocabulary(new Vocabulary(null, type, university, Set.of()));
  }

  private boolean currentUserMayWrite(Vocabulary target) {
    return permissionService.currentUserIsAffiliatedWith(target.university());
  }
}