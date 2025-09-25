package at.ac.tuwien.fundify.domain.common;


import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import java.util.Set;

public record Vocabulary(
    VocabularyId id,
    EVocabularyType type,
    UniversityReference university,
    Set<String> entries
) {

}

