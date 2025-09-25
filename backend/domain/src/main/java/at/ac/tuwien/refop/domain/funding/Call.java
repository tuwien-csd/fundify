package at.ac.tuwien.refop.domain.funding;


import at.ac.tuwien.refop.domain.common.CallOwner;
import at.ac.tuwien.refop.domain.common.*;
import at.ac.tuwien.refop.domain.funding.vo.*;
import at.ac.tuwien.refop.domain.funding.vo.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Call implements Entity<CallId>, Publishable {
    //metadata fields
    private CallId id;
    private EPublicationStatus status;
    private EEntryOrigin entryOrigin;
    private ECallType fundingType;
    private LocalDateTime registrationDate;
    private LocalDateTime lastSync;
    private LocalDateTime lastUpdatedAt;
    // mandatory fields
    private List<TranslatedText> name;
    private List<ETargetGroup> targetGroups;
    private List<StandardizedSubject> subjects;
    private List<EFundingCharacteristic> characteristics;
    private EFundingScheme fundingScheme;
    private ELegalType legalType;
    private MonetaryNumber minProjectVolume;
    private MonetaryNumber maxProjectVolume;
    private EAnswerYN fullyFunded;
    private BigDecimal minInkind;
    private BigDecimal maxOverhead;
    private TimeSpan minProjectDuration;
    private TimeSpan maxProjectDuration;
    private List<CallStage> callStages;
    private List<ELanguage> applicationLanguages;
    private List<EModeOfSubmission> submissionModes;
    private List<Contact> contacts;
    private FunderReference funder;
    private List<FundifyUser> subscriptions;
    // optional fields
    private String acronym;
    private List<TranslatedText> targetGroupSpecified;
    private List<List<TranslatedText>> thematicOrientations;
    private List<TranslatedText> description;
    private List<ECareerStage> careerStages;
    private List<TranslatedText> eligibleApplicants;
    private ERegionalScope eligibleApplicantsScope;
    private List<EAustrianState> eligibleApplicantsRegions;
    private Integer callVolumeProjects;
    private MonetaryNumber callVolumeAmount;
    private List<TranslatedText> inkindDetails;
    private List<TranslatedText> overheadDetails;
    private List<TranslatedText> reportingPeriodDetails;
    private List<EDecisionProcess> decisionProcess;
    private List<TranslatedText> decisionProcessDetails;
    private EAnswerYN dmpRequired;
    private String dmpGuidelines;
    private List<TranslatedText> projectStartDetails;
    private List<String> website;
    private ProgramReference partOf;
    private List<FunderReference> jointCallPartner;
    private CallOwner callOwner;
    private ExternalIdentifier externalIdentifier = new ExternalIdentifier();

    @Override
    public PublisherReference publisherReference() {
        return funder;
    }

    @Override
    public EPublicationStatus publicationStatus() {
        return status;
    }

    public void publish(String publisherRisMemberId) {
        RisId risId = RisId.generateFundingRisId(publisherRisMemberId, this.getId().value());
        this.getExternalIdentifier().setRisId(risId);
        this.setStatus(EPublicationStatus.PUBLISHED);
        LocalDateTime now = LocalDateTime.now();
        this.setRegistrationDate(now);
        this.setLastSync(now);
    }
}