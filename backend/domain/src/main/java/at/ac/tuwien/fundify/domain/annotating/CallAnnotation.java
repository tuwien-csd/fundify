package at.ac.tuwien.fundify.domain.annotating;

import at.ac.tuwien.fundify.domain.common.TranslatedText;

import java.util.List;

public record CallAnnotation (
        List<TranslatedText> internalDeadline,
        List<String> keywords,
        List<String> targetGroups,
        List<WebLink> links,
        UniversityContact contact
) {}