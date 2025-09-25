package at.ac.tuwien.refop.domain.common;


import at.ac.tuwien.refop.domain.annotating.UniversityReference;
import java.util.Set;

public record Vocabulary(
    VocabularyId id,
    EVocabularyType type,
    UniversityReference university,
    Set<String> entries
) {

}

