package at.ac.tuwien.refop.domain.dto;

import at.ac.tuwien.refop.domain.annotating.UniversityReference;
import at.ac.tuwien.refop.domain.common.EPublicationStatus;
import at.ac.tuwien.refop.domain.common.FundifyUser;
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
