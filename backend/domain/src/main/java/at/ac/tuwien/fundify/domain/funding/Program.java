package at.ac.tuwien.fundify.domain.funding;

import at.ac.tuwien.fundify.domain.common.EFundingCharacteristic;
import at.ac.tuwien.fundify.domain.common.EPublicationStatus;
import at.ac.tuwien.fundify.domain.common.ETargetGroup;
import at.ac.tuwien.fundify.domain.common.Entity;
import at.ac.tuwien.fundify.domain.common.ProgramId;
import at.ac.tuwien.fundify.domain.common.Publishable;
import at.ac.tuwien.fundify.domain.common.PublisherReference;
import at.ac.tuwien.fundify.domain.common.RisId;
import at.ac.tuwien.fundify.domain.common.TranslatedText;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ECareerStage;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EEntryOrigin;
import at.ac.tuwien.fundify.domain.funding.vo.enums.EFundingScheme;
import at.ac.tuwien.fundify.domain.funding.vo.enums.ELegalType;
import at.ac.tuwien.fundify.domain.common.*;
import at.ac.tuwien.fundify.domain.common.DateRange;
import at.ac.tuwien.fundify.domain.funding.vo.StandardizedSubject;
import at.ac.tuwien.fundify.domain.funding.vo.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Program implements Entity<ProgramId>, Publishable {
    //meta data fields
    private ProgramId id;
    private EPublicationStatus status;
    private EEntryOrigin entryOrigin;
    private LocalDateTime registrationDate;
    private LocalDateTime lastSync;
    // mandatory fields
    private List<TranslatedText> name;
    private List<ETargetGroup> targetGroups;
    private List<StandardizedSubject> subjects;
    private List<TranslatedText> description;
    private List<EFundingCharacteristic> characteristics;
    private List<String> website;
    private EFundingScheme fundingScheme;
    private ELegalType legalType;
    private FunderReference funder;
    // optional fields
    private String acronym;
    private List<List<TranslatedText>> programTracks;
    private List<ECareerStage> careerStages;
    private DateRange duration;


    private ExternalIdentifier externalIdentifier = new ExternalIdentifier();

    public void publish(String publisherRisMemberId) {
        RisId risId = RisId.generateFundingRisId(publisherRisMemberId, this.getId().value());
        this.getExternalIdentifier().setRisId(risId);
        this.setStatus(EPublicationStatus.PUBLISHED);
        LocalDateTime now = LocalDateTime.now();
        this.setRegistrationDate(now);
        this.setLastSync(now);
    }

    @Override
    public PublisherReference publisherReference() {
        return funder;
    }

    @Override
    public EPublicationStatus publicationStatus() {
        return status;
    }
}
