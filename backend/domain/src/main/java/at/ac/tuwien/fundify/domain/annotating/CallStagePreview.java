package at.ac.tuwien.fundify.domain.annotating;

import at.ac.tuwien.fundify.domain.common.DateRange;

public record CallStagePreview(
        int number,
        DateRange duration
) {}