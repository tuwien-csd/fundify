package at.ac.tuwien.fundify.domain.funding.vo;

import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.common.TranslatedText;

import java.util.List;

public record CallStage(
        int number,
        DateRange duration,
        List<TranslatedText> description
) {}