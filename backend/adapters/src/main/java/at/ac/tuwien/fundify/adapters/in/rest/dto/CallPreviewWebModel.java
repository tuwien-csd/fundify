package at.ac.tuwien.fundify.adapters.in.rest.dto;


import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.EFundingCharacteristicWebModel;
import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.ETargetGroupWebModel;

import java.time.LocalDateTime;
import java.util.List;

public record CallPreviewWebModel(
        String id,
        LocalDateTime registrationDate,
        LocalDateTime lastSync,
        List<TranslatedTextWebModel> name,
        AnnotatingProgramRefWebModel partOf,
        AnnotatingFunderRefWebModel funder,
        List<ETargetGroupWebModel> targetGroups,
        List<EFundingCharacteristicWebModel> characteristics,
        List<CallStagePreviewWebModel> callStages
) {
}