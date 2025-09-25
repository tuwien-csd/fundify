package at.ac.tuwien.refop.adapters.in.rest.dto;

import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EAnswerYNWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EAustrianStateWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ECallTypeWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ECareerStageWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EDecisionProcessWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EEntryOriginWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EFundingCharacteristicWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EFundingSchemeWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ELanguageWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ELegalTypeWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EModeOfSubmissionWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.EPublicationStatusWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ERegionalScopeWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ESubscriptionStatusWebModel;
import at.ac.tuwien.refop.adapters.in.rest.dto.enums.ETargetGroupWebModel;
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