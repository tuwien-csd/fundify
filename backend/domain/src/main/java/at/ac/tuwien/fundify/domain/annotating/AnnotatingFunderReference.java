package at.ac.tuwien.fundify.domain.annotating;

import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.common.FunderId;

import java.util.List;

public record AnnotatingFunderReference(
        FunderId id,
        String acronym,
        List<TranslatedText> name
) {
}