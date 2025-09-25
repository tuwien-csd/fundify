package at.ac.tuwien.refop.adapters.in.rest.dto;

import at.ac.tuwien.refop.domain.common.PublisherReference;

public record UniversityRefWebModel(
        String id,
        String emailDomain,
        String acronym
) implements PublisherReference {
    @Override
    public String publisherId() {
        return id;
    }
}