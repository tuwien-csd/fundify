package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.domain.common.PublisherReference;

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