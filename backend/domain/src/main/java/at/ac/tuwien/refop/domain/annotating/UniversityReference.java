package at.ac.tuwien.refop.domain.annotating;

import at.ac.tuwien.refop.domain.common.PublisherReference;
import at.ac.tuwien.refop.domain.common.RisId;

public record UniversityReference(
        UniversityId id,
        RisId risId,
        String emailDomain,
        String acronym
) implements PublisherReference {

    @Override
    public String publisherId() {
        return id.value();
    }
}