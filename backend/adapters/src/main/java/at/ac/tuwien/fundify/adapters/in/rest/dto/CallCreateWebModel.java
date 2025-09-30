package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record CallCreateWebModel(
        // mandatory fields according to RIS Synergy funding API 1.1 specification
        @Schema(required = true) @NotNull
        ECallTypeWebModel fundingType,
        @Schema(required = true) @NotNull @Size(min = 1) @Valid
        List<TranslatedTextWebModel> name,
        @Schema(required = true) @NotNull @Valid
        FunderRefWebModel funder,
        @Schema(required = true) @NotNull @Size(min = 1)
        List<EFundingCharacteristicWebModel> characteristics,
        @Schema(required = true) @NotNull @Size(min = 1)
        List<ETargetGroupWebModel> targetGroups,
        @Schema(required = true) @NotNull @Size(min = 1) @Valid
        List<StandardizedSubjectWebModel> subjects,
        @Schema(required = true) @NotNull
        EFundingSchemeWebModel fundingScheme,
        @Schema(required = true) @NotNull
        ELegalTypeWebModel legalType,
        @Schema(required = true) @NotNull @Size(min = 1)
        List<EModeOfSubmissionWebModel> submissionModes,
        @Schema(required = true) @NotNull @Valid
        MonetaryNumberWebModel minProjectVolume,
        @Schema(required = true) @NotNull @Valid
        MonetaryNumberWebModel maxProjectVolume,
        @Schema(required = true) @NotNull
        EAnswerYNWebModel fullyFunded,
        @Schema(required = true) @NotNull @DecimalMin("0")
        BigDecimal minInkind,
        @Schema(required = true) @NotNull @DecimalMin("0")
        BigDecimal maxOverhead,
        @Schema(required = true) @NotNull @Valid
        TimeSpanWebModel minProjectDuration,
        @Schema(required = true) @NotNull @Valid
        TimeSpanWebModel maxProjectDuration,
        @Schema(required = true) @NotNull @Size(min = 1)
        List<ELanguageWebModel> applicationLanguages,

        // optional fields according to RIS Synergy funding API 1.1 specification
        @Valid
        List<IdentifierWebModel> identifiers,
        String acronym,
        List<TranslatedTextWebModel> description,
        List<ECareerStageWebModel> careerStages,
        List<String> website,
        Integer callVolumeProjects,
        @Valid
        List<CallStageWebModel> callStages,
        @Valid
        List<FunderContactWebModel> contacts,
        @Valid
        MonetaryNumberWebModel callVolumeAmount,
        List<EDecisionProcessWebModel> decisionProcess,
        List<TranslatedTextWebModel> decisionProcessDetails,
        EAnswerYNWebModel dmpRequired,
        String dmpGuidelines,
        List<TranslatedTextWebModel> projectStartDetails,
        List<TranslatedTextWebModel> targetGroupSpecified,
        List<TranslatedTextWebModel> eligibleApplicants,
        ERegionalScopeWebModel eligibleApplicantsScope,
        List<EAustrianStateWebModel> eligibleApplicantsRegions,
        List<TranslatedTextWebModel> inkindDetails,
        List<TranslatedTextWebModel> overheadDetails,
        List<TranslatedTextWebModel> reportingPeriodDetails,
        ProgramRefWebModel partOf,
        List<List<TranslatedTextWebModel>> thematicOrientations,
        @Valid
        List<FunderRefWebModel> jointCallPartner,

        // internal fields
        @Schema(required = true) @NotNull
        EPublicationStatusWebModel status,
        String risId
) {

}
