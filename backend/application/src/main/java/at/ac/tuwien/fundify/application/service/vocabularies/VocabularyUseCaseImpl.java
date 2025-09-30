package at.ac.tuwien.fundify.application.service.vocabularies;

import at.ac.tuwien.fundify.application.port.common.UserService;
import at.ac.tuwien.fundify.application.port.in.vocabularies.VocabularyUseCase;
import at.ac.tuwien.fundify.application.port.out.persistence.UniversityQuery;
import at.ac.tuwien.fundify.application.port.out.persistence.VocabularyQuery;
import at.ac.tuwien.fundify.application.port.out.persistence.VocabularyRepository;
import at.ac.tuwien.fundify.application.service.common.BasePermissionService;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import at.ac.tuwien.fundify.domain.common.EVocabularyType;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import at.ac.tuwien.fundify.domain.common.exceptions.FundifyException;
import at.ac.tuwien.fundify.domain.common.exceptions.InsufficientPermissionsException;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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

    if (!currentUserMayWrite(vocabulary)) {
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

    if (!currentUserMayWrite(vocabulary)) {
      throw new InsufficientPermissionsException(id.value());
    }

    vocabulary.entries().remove(entry);
    return vocabularyRepository.updateVocabulary(vocabulary);
  }

  @Override
  public Vocabulary getById(VocabularyId id) throws EntityNotFoundException {
    return vocabularyQuery
        .findById(id)
        .filter(currentUserCanRead())
        .orElseThrow(() -> EntityNotFoundException.vocabularyNotFound(id.value()));
  }

  @Override
  public List<Vocabulary> getAll() {
    List<Vocabulary> vocabularies = vocabularyQuery
        .findAll()
        .stream()
        .filter(currentUserCanRead())
        .toList();

    if (!vocabularies.isEmpty()) {
      return vocabularies;
    }

    // Vocabularies should always exist for annotators. If the list is empty, it probably has not been initialized yet.
    var currentUserUniversity = universityQuery.findByAcronym(
        userService.getCurrentUserAffiliationId()).orElseThrow(
        () -> EntityNotFoundException.universityNotFound(
            userService.getCurrentUserAffiliationId()));
    return initializeVocabulariesForUniversity(currentUserUniversity.getId());
  }

  private List<Vocabulary> initializeVocabulariesForUniversity(UniversityId universityId)
      throws EntityNotFoundException {
    log.infof("Initialization of vocabularies for university %s initiated by user %s",
        universityId.value(), userService.getCurrentUserIdAndName());
    List<Vocabulary> vocabularies = new ArrayList<>();
    if (vocabularyQuery.findByUniversityId(universityId).isEmpty()) {
      for (EVocabularyType type : EVocabularyType.values()) {
        vocabularies.add(initVocabulary(universityId, type));
      }
    }
    return vocabularies;
  }

  private Vocabulary initVocabulary(UniversityId universityId, EVocabularyType type)
      throws EntityNotFoundException {
    UniversityReference university = universityQuery.findReferenceById(universityId)
        .orElseThrow(() -> EntityNotFoundException.universityNotFound(universityId.value()));
    return vocabularyRepository.persistVocabulary(new Vocabulary(null, type, university, Set.of()));
  }

  /**
   * Predicate to check if the current user can read the annotated call. Important note: If you use
   * this on optionals to filter, you might send a NOT_FOUND exception, when the user really just
   * has no permission to read the item. Depending on your logic, this can be fine.
   */
  private Predicate<Vocabulary> currentUserCanRead() {
    return item -> permissionService.currentUserIsAffiliatedWith(item.university());
  }

  private boolean currentUserMayWrite(Vocabulary target) {
    return permissionService.currentUserIsAffiliatedWith(target.university());
  }
}