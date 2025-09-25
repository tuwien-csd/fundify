package at.ac.tuwien.refop.adapters.in.rest.dto;


import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EVocabularyTypeWebModel;
import java.util.Set;

public record VocabularyWebModel(
    String id,
    EVocabularyTypeWebModel type,
    UniversityRefWebModel university,
    Set<String> entries
) {

}