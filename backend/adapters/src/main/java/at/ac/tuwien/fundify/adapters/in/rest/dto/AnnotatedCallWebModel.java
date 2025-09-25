package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EPublicationStatusWebModel;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.FundifyUser;
import at.ac.tuwien.fundify.domain.common.Publishable;
import at.ac.tuwien.fundify.domain.common.PublisherReference;
import java.time.LocalDateTime;

public record AnnotatedCallWebModel(
        String id,
        CallPreviewWebModel callPreview,
        EPublicationStatusWebModel status,
        CallAnnotationWebModel annotation,
        UniversityRefWebModel university,
        LocalDateTime lastUpdatedAt,
        FundifyUser lastUpdatedBy
) implements Publishable {

    public EPublicationStatus publicationStatus() {
        return this.status == EPublicationStatusWebModel.DRAFT ? EPublicationStatus.DRAFT : EPublicationStatus.PUBLISHED;
    }

    public PublisherReference publisherReference() {
        return this.university;
    }
}