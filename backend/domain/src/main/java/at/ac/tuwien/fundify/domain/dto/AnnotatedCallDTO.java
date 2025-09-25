package at.ac.tuwien.fundify.domain.dto;

import at.ac.tuwien.fundify.domain.annotating.UniversityReference;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.FundifyUser;
import java.time.LocalDateTime;

public record AnnotatedCallDTO(
        String id,
        CallDTO call,
        EPublicationStatus status,
        CallAnnotationDTO annotation,
        UniversityReference universityReference,
        LocalDateTime lastUpdatedAt,
        FundifyUser lastUpdatedBy) {
}
