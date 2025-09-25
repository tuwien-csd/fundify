package at.ac.tuwien.refop.adapters.out.persistence.mongo.funding;

import at.ac.tuwien.refop.domain.common.*;
import at.ac.tuwien.refop.domain.funding.vo.*;
import at.ac.tuwien.refop.domain.funding.vo.enums.*;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@MongoEntity(database = "refop", collection = "calls")
public class CallMongoEntity extends PanacheMongoEntity {
    //metadata fields
    public EPublicationStatus status;
    public ECallType fundingType;
    public EEntryOrigin entryOrigin;
    public LocalDateTime registrationDate;
    public LocalDateTime lastSync;
    public LocalDateTime lastUpdatedAt;
    // mandatory fields
    public String risId;
    public List<TranslatedText> name;
    public List<ETargetGroup> targetGroups;
    public List<StandardizedSubject> subjects;
    public List<EFundingCharacteristic> characteristics;
    public EFundingScheme fundingScheme;
    public ELegalType legalType;
    public MonetaryNumber minProjectVolume;
    public MonetaryNumber maxProjectVolume;
    public EAnswerYN fullyFunded;
    public BigDecimal minInkind;
    public BigDecimal maxOverhead;
    public TimeSpan minProjectDuration;
    public TimeSpan maxProjectDuration;
    public List<CallStage> callStages;
    public List<ELanguage> applicationLanguages;
    public List<EModeOfSubmission> submissionModes;
    public List<Contact> contacts;
    public ObjectId funderId;
    // optional fields
    public String acronym;
    public List<Identifier> identifiers;
    public List<TranslatedText> targetGroupSpecified;
    public List<List<TranslatedText>> thematicOrientations;
    public List<TranslatedText> description;
    public List<ECareerStage> careerStages;
    public List<TranslatedText> eligibleApplicants;
    public ERegionalScope eligibleApplicantsScope;
    public List<EAustrianState> eligibleApplicantsRegions;
    public Integer callVolumeProjects;
    public MonetaryNumber callVolumeAmount;
    public List<TranslatedText> inkindDetails;
    public List<TranslatedText> overheadDetails;
    public List<TranslatedText> reportingPeriodDetails;
    public List<EDecisionProcess> decisionProcess;
    public List<TranslatedText> decisionProcessDetails;
    public EAnswerYN dmpRequired;
    public String dmpGuidelines;
    public List<TranslatedText> projectStartDetails;
    public List<String> website;
    public ObjectId partOfId;
    public List<ObjectId> registeredJointCallPartnerIds;
    public List<FunderReferenceProjection> otherJointCallpartner;
    public List<FundifyUser> subscriptions;
    public String ownerKind;
    public String ownerId;
}