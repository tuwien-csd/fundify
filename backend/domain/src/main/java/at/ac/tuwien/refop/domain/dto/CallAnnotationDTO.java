package at.ac.tuwien.refop.domain.dto;

import at.ac.tuwien.refop.domain.common.TranslatedText;

import java.util.List;

public record CallAnnotationDTO(
        List<TranslatedText> internalDeadline,
        List<String> keywords,
        List<String> targetGroups,
        List<WebLinkDTO> links,
        UniversityContactDTO contact
) {}