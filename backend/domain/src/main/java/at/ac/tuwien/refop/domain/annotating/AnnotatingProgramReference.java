package at.ac.tuwien.refop.domain.annotating;

import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.common.ProgramId;

import java.util.List;

public record AnnotatingProgramReference(
        ProgramId id,
        String acronym,
        List<TranslatedText> name
) {

}