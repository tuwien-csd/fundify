package at.ac.tuwien.fundify.domain.annotating;

import lombok.NonNull;

public record AnnotatedCallId(@NonNull String value) {
}