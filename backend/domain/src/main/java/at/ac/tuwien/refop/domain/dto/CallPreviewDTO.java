package at.ac.tuwien.refop.domain.dto;

import at.ac.tuwien.refop.domain.annotating.AnnotatingFunderReference;
import at.ac.tuwien.refop.domain.annotating.AnnotatingProgramReference;
import at.ac.tuwien.refop.domain.annotating.CallStagePreview;
import at.ac.tuwien.refop.domain.common.TranslatedText;
import at.ac.tuwien.refop.domain.common.EFundingCharacteristic;
import at.ac.tuwien.refop.domain.common.ETargetGroup;

import java.time.LocalDateTime;
import java.util.List;

public record CallPreviewDTO(
        String id,
        LocalDateTime registrationDate,
        LocalDateTime lastSync,
        List<TranslatedText> name,
        AnnotatingProgramReference partOf,
        AnnotatingFunderReference funder,
        List<ETargetGroup> targetGroups,
        List<EFundingCharacteristic> characteristics,
        List<CallStagePreview> callStages
) {
}