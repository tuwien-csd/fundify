package at.ac.tuwien.fundify.domain.dto;

import at.ac.tuwien.fundify.domain.common.TranslatedText;

import java.util.List;

public record CallAnnotationDTO(
        List<TranslatedText> internalDeadline,
        List<String> keywords,
        List<String> targetGroups,
        List<WebLinkDTO> links,
        UniversityContactDTO contact
) {}