package at.ac.tuwien.refop.domain.common;

public enum EPublicationStatus {
    DRAFT,
    PUBLISHED;

    public static final EPublicationStatus defaultToPublished(EPublicationStatus status) {
        if (status == null) {
            return EPublicationStatus.PUBLISHED;
        }
        return status;
    }
}
