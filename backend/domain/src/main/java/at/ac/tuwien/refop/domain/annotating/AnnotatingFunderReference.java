package at.ac.tuwien.refop.domain.annotating;

import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.common.FunderId;

import java.util.List;

public record AnnotatingFunderReference(
        FunderId id,
        String acronym,
        List<TranslatedText> name
) {
}