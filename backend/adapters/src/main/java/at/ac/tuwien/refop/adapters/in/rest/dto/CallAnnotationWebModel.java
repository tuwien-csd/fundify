package at.ac.tuwien.refop.adapters.in.rest.dto;

import java.util.List;

public record CallAnnotationWebModel(
        List<TranslatedTextWebModel> internalDeadline,
        List<String> keywords,
        List<String> targetGroups,
        List<WebLinkWebModel> links,
        UniversityContactWebModel contact
) {
}