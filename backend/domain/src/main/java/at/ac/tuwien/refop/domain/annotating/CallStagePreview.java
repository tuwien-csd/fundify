package at.ac.tuwien.refop.domain.annotating;

import at.ac.tuwien.refop.domain.common.DateRange;

public record CallStagePreview(
        int number,
        DateRange duration
) {}