package at.ac.tuwien.refop.adapters.in.rest.dto;

import java.util.List;

public record AnnotatingProgramRefWebModel(
        String id,
        String acronym,
        List<TranslatedTextWebModel> name
) {
}