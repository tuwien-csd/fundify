package at.ac.tuwien.fundify.domain.annotating;

import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.common.ProgramId;

import java.util.List;

public record AnnotatingProgramReference(
        ProgramId id,
        String acronym,
        List<TranslatedText> name
) {

}