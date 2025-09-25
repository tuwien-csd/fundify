package at.ac.tuwien.refop.domain.annotating;

import at.ac.tuwien.refop.domain.common.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnotatedCall implements Entity<AnnotatedCallId>, Publishable {
    private AnnotatedCallId id;
    private CallId callId;
    private UniversityReference universityReference;
    private EPublicationStatus status;
    private CallAnnotation annotation;
    private LocalDateTime lastUpdatedAt;
    private FundifyUser lastUpdatedBy;

    public static AnnotatedCall createNew(CallId callId, UniversityReference universityReference, CallAnnotation callAnnotation, LocalDateTime lastUpdatedAt, FundifyUser lastUpdatedBy) {
        return new AnnotatedCall(
                null,
                callId,
                universityReference,
                EPublicationStatus.DRAFT,
                callAnnotation,
                lastUpdatedAt,
                lastUpdatedBy
        );
    }

    public void publish() {
        this.status = EPublicationStatus.PUBLISHED;
    }

    public void updateAnnotation(CallAnnotation callAnnotation) {
        this.annotation = callAnnotation;
    }

    public EPublicationStatus publicationStatus() {
        return this.status == EPublicationStatus.DRAFT ? EPublicationStatus.DRAFT : EPublicationStatus.PUBLISHED;
    }

    public PublisherReference publisherReference() {
        return this.universityReference;
    }
}