package at.ac.tuwien.fundify.adapters.in.rest.dto;

import at.ac.tuwien.fundify.adapters.in.rest.dto.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record CallWebModel (
        @Schema(required = true)
        String id,
        //metadata fields
        @Schema(required = true)
        EPublicationStatusWebModel status,
        @Schema(required = true)
        ECallTypeWebModel fundingType,
        @Schema(required = true)
        EEntryOriginWebModel entryOrigin,
        @Schema(required = true)
        LocalDateTime registrationDate,
        @Schema(required = true)
        LocalDateTime lastSync,
        @Schema(required = true)
        LocalDateTime lastUpdatedAt,
        // mandatory fields
        @Schema(required = true)
        String risId,
        @Schema(required = true)
        List<TranslatedTextWebModel> name,
        @Schema(required = true)
        List<ETargetGroupWebModel> targetGroups,
        @Schema(required = true)
        List<StandardizedSubjectWebModel> subjects,
        @Schema(required = true)
        List<EFundingCharacteristicWebModel> characteristics,
        @Schema(required = true)
        EFundingSchemeWebModel fundingScheme,
        @Schema(required = true)
        ELegalTypeWebModel legalType,
        @Schema(required = true)
        MonetaryNumberWebModel minProjectVolume,
        @Schema(required = true)
        MonetaryNumberWebModel maxProjectVolume,
        @Schema(required = true)
        EAnswerYNWebModel fullyFunded,
        @Schema(required = true)
        BigDecimal minInkind,
        @Schema(required = true)
        BigDecimal maxOverhead,
        @Schema(required = true)
        TimeSpanWebModel minProjectDuration,
        @Schema(required = true)
        TimeSpanWebModel maxProjectDuration,
        @Schema(required = true)
        List<CallStageWebModel> callStages,
        @Schema(required = true)
        List<ELanguageWebModel> applicationLanguages,
        @Schema(required = true)
        List<EModeOfSubmissionWebModel> submissionModes,
        @Schema(required = true)
        List<FunderContactWebModel> contacts,
        @Schema(required = true)
        FunderRefWebModel funder,
        @Schema(required = true)
        ESubscriptionStatusWebModel subscriptionStatus,
        // optional fields
        String acronym,
        List<IdentifierWebModel> identifiers,
        List<TranslatedTextWebModel> targetGroupSpecified,
        List<List<TranslatedTextWebModel>> thematicOrientations,
        List<TranslatedTextWebModel> description,
        List<ECareerStageWebModel> careerStages,
        List<TranslatedTextWebModel> eligibleApplicants,
        ERegionalScopeWebModel eligibleApplicantsScope,
        List<EAustrianStateWebModel> eligibleApplicantsRegions,
        List<ECountryWebModel> eligibleTargetRegions,
        List<ECountryWebModel> eligibleSourceRegions,
        Integer callVolumeProjects,
        MonetaryNumberWebModel callVolumeAmount,
        List<TranslatedTextWebModel> inkindDetails,
        List<TranslatedTextWebModel> overheadDetails,
        List<TranslatedTextWebModel> reportingPeriodDetails,
        List<EDecisionProcessWebModel> decisionProcess,
        List<TranslatedTextWebModel> decisionProcessDetails,
        EAnswerYNWebModel dmpRequired,
        String dmpGuidelines,
        List<TranslatedTextWebModel> projectStartDetails,
        List<String> website,
        ProgramRefWebModel partOf,
        List<FunderRefWebModel> jointCallPartner,
        CallOwnerWebModel callOwner
) implements WebModel {

    public String publisherId() {
        return funder != null ? funder.id() : null;
    }
}