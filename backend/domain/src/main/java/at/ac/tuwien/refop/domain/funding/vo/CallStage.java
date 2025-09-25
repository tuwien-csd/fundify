package at.ac.tuwien.refop.domain.funding.vo;

import at.ac.tuwien.refop.domain.common.DateRange;
import at.ac.tuwien.refop.domain.common.TranslatedText;

import java.util.List;

public record CallStage(
        int number,
        DateRange duration,
        List<TranslatedText> description
) {}