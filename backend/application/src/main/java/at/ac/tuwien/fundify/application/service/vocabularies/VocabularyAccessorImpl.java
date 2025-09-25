package at.ac.tuwien.fundify.application.service.vocabularies;

import at.ac.tuwien.fundify.application.port.in.vocabularies.VocabularyAccessor;
import at.ac.tuwien.fundify.application.port.out.persistence.VocabularyQuery;
import at.ac.tuwien.fundify.application.service.common.BasePermissionService;
import at.ac.tuwien.fundify.domain.annotating.UniversityId;
import at.ac.tuwien.fundify.domain.common.Vocabulary;
import at.ac.tuwien.fundify.domain.common.VocabularyId;
import at.ac.tuwien.fundify.domain.common.exceptions.EntityNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class VocabularyAccessorImpl implements VocabularyAccessor {

  private final VocabularyQuery vocabularyQuery;
  private final BasePermissionService basePermissionService;

  @Override
  public Vocabulary getById(VocabularyId id) throws EntityNotFoundException {
    return vocabularyQuery
        .findById(id)
        .filter(currentUserCanRead())
        .orElseThrow(() -> EntityNotFoundException.vocabularyNotFound(id.value()));
  }

  @Override
  public List<Vocabulary> getByUniversityId(UniversityId universityId) {
    return vocabularyQuery
        .findByUniversityId(universityId)
        .stream().filter(currentUserCanRead())
        .toList();
  }

  @Override
  public List<Vocabulary> getAll() {
    return vocabularyQuery
        .findAll()
        .stream().filter(currentUserCanRead())
        .toList();
  }

  /**
   * Predicate to check if the current user can read the annotated call. Important note: If you use
   * this on optionals to filter, you might send a NOT_FOUND exception, when the user really just
   * has no permission to read the item. Depending on your logic, this can be fine.
   */
  private Predicate<Vocabulary> currentUserCanRead() {
    return item -> basePermissionService.currentUserIsAffiliatedWith(item.university());
  }

}